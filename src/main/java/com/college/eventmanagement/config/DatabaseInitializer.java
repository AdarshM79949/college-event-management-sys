package com.college.eventmanagement.config;

import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.model.Venue;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.repository.VenueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Order(1)
@Slf4j
@SuppressWarnings("null")
public class DatabaseInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DatabaseInitializer(UserRepository userRepository,
                               VenueRepository venueRepository,
                               EventRepository eventRepository,
                               PasswordEncoder passwordEncoder,
                               org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing baseline data for MGM College of Engineering...");
        seedAdminAndOrganizers();
        seedVenues();
        seedInitialClubEvents();
        alignIdentitySequences();
        log.info("Baseline data initialization complete. Ready for desktop app logins.");
    }

    private void alignIdentitySequences() {
        try {
            Long maxEventVal = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM events", Long.class);
            long nextEventId = (maxEventVal != null ? maxEventVal : 0L) + 1;
            jdbcTemplate.execute("ALTER TABLE events ALTER COLUMN id RESTART WITH " + nextEventId);

            Long maxUserVal = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM users", Long.class);
            long nextUserId = (maxUserVal != null ? maxUserVal : 0L) + 1;
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH " + nextUserId);

            Long maxRegVal = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM registrations", Long.class);
            long nextRegId = (maxRegVal != null ? maxRegVal : 0L) + 1;
            jdbcTemplate.execute("ALTER TABLE registrations ALTER COLUMN id RESTART WITH " + nextRegId);

            Long maxVenueVal = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM venues", Long.class);
            long nextVenueId = (maxVenueVal != null ? maxVenueVal : 0L) + 1;
            jdbcTemplate.execute("ALTER TABLE venues ALTER COLUMN id RESTART WITH " + nextVenueId);
            log.info("Aligned H2 identity sequences (Events next: {}, Users next: {})", nextEventId, nextUserId);
        } catch (Exception ex) {
            log.warn("Could not align identity sequences: {}", ex.getMessage());
        }
    }

    private void seedAdminAndOrganizers() {
        // 1. System Administrator (Admin)
        upsertUser("admin@campus.edu", "admin123", "System Administrator", Role.ROLE_ADMIN,
                "FAC-ADM-00", "MGM College of Engineering - Administration", "Faculty");

        // 2. Dean of Student Affairs (Admin)
        upsertUser("dean@campus.edu", "admin123", "Dean of Student Affairs", Role.ROLE_ADMIN,
                "FAC-MGM-01", "MGM College of Engineering - Administration", "Faculty");

        // 3. Chief Campus Proctor (Admin)
        upsertUser("proctor@campus.edu", "admin123", "Chief Campus Proctor", Role.ROLE_ADMIN,
                "FAC-PROCT-02", "MGM College of Engineering - Proctorial", "Faculty");

        // 3. ACM & Coding Club Head (Organizer)
        upsertUser("acm.chapter@campus.edu", "org123", "ACM & Coding Club Head", Role.ROLE_ORGANIZER,
                "CCV-ORG-ACM", "Computer Science & Engineering", "Lead / Faculty");

        // 4. Cultural Council (Organizer)
        upsertUser("cultural.council@campus.edu", "org123", "Fine Arts & Music Lead", Role.ROLE_ORGANIZER,
                "CCV-ORG-CUL", "Student Cultural Council", "Lead / Faculty");

        // 5. Sports Council (Organizer)
        upsertUser("sports.council@campus.edu", "org123", "Sports Council Secretary", Role.ROLE_ORGANIZER,
                "CCV-ORG-SPT", "Physical Education & Athletics", "Lead / Faculty");

        // 6. E-Cell Student President (Organizer)
        upsertUser("ecell.lead@campus.edu", "org123", "E-Cell Student President", Role.ROLE_ORGANIZER,
                "CCV-ORG-ECL", "Innovation & Entrepreneurship", "Lead / Faculty");
    }

    private void upsertUser(String email, String rawPassword, String name, Role role,
                            String collegeId, String branch, String semester) {
        userRepository.findByEmail(email).ifPresentOrElse(user -> {
            boolean needsUpdate = false;
            if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                needsUpdate = true;
            }
            if (!collegeId.equals(user.getCollegeId())) {
                user.setCollegeId(collegeId);
                needsUpdate = true;
            }
            if (user.getRole() != role) {
                user.setRole(role);
                needsUpdate = true;
            }
            if (needsUpdate) {
                userRepository.save(user);
                log.info("Updated pre-configured user credentials: {}", email);
            }
        }, () -> {
            User newUser = User.builder()
                    .name(name)
                    .email(email)
                    .password(passwordEncoder.encode(rawPassword))
                    .role(role)
                    .collegeId(collegeId)
                    .branch(branch)
                    .semester(semester)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(newUser);
            log.info("Created baseline user: {} ({})", email, role);
        });
    }

    private void seedVenues() {
        if (venueRepository.count() == 0) {
            venueRepository.save(Venue.builder()
                    .name("Dr. A.P.J. Abdul Kalam Grand Auditorium")
                    .type("Auditorium")
                    .capacity(1200)
                    .location("MGM Campus - Central Auditorium Block")
                    .facilities("Dolby 7.1 Surround, Dual 4K Laser Projectors, Central AC, Green Rooms")
                    .imageUrl("images/auditorium.jpg")
                    .build());

            venueRepository.save(Venue.builder()
                    .name("MGM Open Air Amphitheatre")
                    .type("Amphitheater")
                    .capacity(2500)
                    .location("Lakeside Arena - MGM East Quad")
                    .facilities("Concert Stage, High-Power Line Array Sound, Intelligent Stage Lighting")
                    .imageUrl("images/amphitheatre.jpg")
                    .build());

            venueRepository.save(Venue.builder()
                    .name("Turing AI & High-Performance Computing Labs")
                    .type("Tech Lab")
                    .capacity(350)
                    .location("MGM Block C - CS & AIML Wing")
                    .facilities("10Gbps Dedicated Fiber, 150 NVIDIA RTX GPU Workstations, Smart Boards")
                    .imageUrl("images/tech_lab.jpg")
                    .build());

            venueRepository.save(Venue.builder()
                    .name("Major Dhyan Chand Sports Complex")
                    .type("Indoor Stadium")
                    .capacity(1800)
                    .location("MGM Sports Pavilion - West Campus")
                    .facilities("Maple Wood Basketball Court, Badminton Courts, LED Scoreboard Jumbotron")
                    .imageUrl("images/sports_complex.jpg")
                    .build());

            venueRepository.save(Venue.builder()
                    .name("Ramanujan Academic Conference Center")
                    .type("Conference Hall")
                    .capacity(450)
                    .location("MGM Block B - Seminar Hall")
                    .facilities("Tiered Executive Seating, Video Conferencing Rig, Translation Booths")
                    .imageUrl("images/conference_center.jpg")
                    .build());

            venueRepository.save(Venue.builder()
                    .name("Vikram Sarabhai Innovation & Incubation Hub")
                    .type("Innovation Hub")
                    .capacity(200)
                    .location("MGM Incubation Tower - Floor 3")
                    .facilities("Pitch Deck Stage, Breakout Discussion Pods, High-Speed WiFi, Coffee Bar")
                    .imageUrl("images/incubation_hub.jpg")
                    .build());

            log.info("Initialized 6 MGM campus venues.");
        }
    }

    private void seedInitialClubEvents() {
        if (eventRepository.count() == 0) {
            User acmLead = userRepository.findByEmail("acm.chapter@campus.edu").orElse(null);
            User culturalLead = userRepository.findByEmail("cultural.council@campus.edu").orElse(null);
            User sportsLead = userRepository.findByEmail("sports.council@campus.edu").orElse(null);
            User ecellLead = userRepository.findByEmail("ecell.lead@campus.edu").orElse(null);

            Venue auditorium = venueRepository.findAll().stream().filter(v -> v.getName().contains("Auditorium")).findFirst().orElse(null);
            Venue amphitheatre = venueRepository.findAll().stream().filter(v -> v.getName().contains("Amphitheatre")).findFirst().orElse(null);
            Venue techLab = venueRepository.findAll().stream().filter(v -> v.getName().contains("Turing")).findFirst().orElse(null);
            Venue sportsComplex = venueRepository.findAll().stream().filter(v -> v.getName().contains("Sports")).findFirst().orElse(null);
            Venue confCenter = venueRepository.findAll().stream().filter(v -> v.getName().contains("Conference")).findFirst().orElse(null);

            // 1. AI & Innovation Summit (Approved)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("AI & Innovation Summit")
                    .tagline("Explore Generative AI, Robotics, and Autonomous Agents")
                    .description("Keynote sessions and hands-on workshops exploring next-gen artificial intelligence and computing.")
                    .category("Tech")
                    .department("Computer Science & Engineering")
                    .eventDate(java.time.LocalDate.now().plusDays(5))
                    .eventTime("10:00 AM")
                    .venue(auditorium)
                    .maxSeats(300)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.APPROVED)
                    .createdBy(acmLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 2. CodeSprint 2026 (Approved)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("CodeSprint 2026")
                    .tagline("Competitive 24-Hour Coding Battle")
                    .description("National speed coding challenge across Algorithms, Data Structures, and Cloud Systems.")
                    .category("Tech")
                    .department("Computer Science & Engineering")
                    .eventDate(java.time.LocalDate.now().plusDays(12))
                    .eventTime("09:00 AM")
                    .venue(techLab)
                    .maxSeats(200)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.APPROVED)
                    .createdBy(acmLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 3. Cultural Fest: Rhythm & Raag (Approved)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Cultural Fest: Rhythm & Raag")
                    .tagline("MGM Annual Cultural Star Night & Musical Concert")
                    .description("Live celebrity bands, classical dance, and EDM headline festival at MGM arena.")
                    .category("Cultural")
                    .department("Student Cultural Council")
                    .eventDate(java.time.LocalDate.now().plusDays(25))
                    .eventTime("05:00 PM")
                    .venue(amphitheatre)
                    .maxSeats(1500)
                    .registeredCount(0)
                    .priceType("Paid")
                    .price(150.0)
                    .status(com.college.eventmanagement.model.EventStatus.APPROVED)
                    .createdBy(culturalLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 4. Career Guidance Workshop (Approved)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Career Guidance Workshop")
                    .tagline("Tech Career Pathways and Global Placement Mentorship")
                    .description("Interactive sessions with alumni and tech recruiters on interview mastery and resume crafting.")
                    .category("Workshop")
                    .department("Training & Placement Cell")
                    .eventDate(java.time.LocalDate.now().plusDays(40))
                    .eventTime("10:00 AM")
                    .venue(confCenter)
                    .maxSeats(150)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.APPROVED)
                    .createdBy(acmLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 5. Sports Meet 2026 (Approved)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Sports Meet 2026")
                    .tagline("Inter-Collegiate Athletics and Arena Championships")
                    .description("Basketball, Badminton, Table Tennis, and indoor athletic competitions.")
                    .category("Sports")
                    .department("Physical Education & Athletics")
                    .eventDate(java.time.LocalDate.now().plusDays(55))
                    .eventTime("09:00 AM")
                    .venue(sportsComplex)
                    .maxSeats(800)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.APPROVED)
                    .createdBy(sportsLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 6. TechNova 2026 (Pending Approval for Dean)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("TechNova 2026")
                    .tagline("Flagship National Hackathon and Tech Expo")
                    .description("36-hour national hackathon proposed by ACM Chapter for Dean approval.")
                    .category("Tech")
                    .department("Computer Science & Engineering")
                    .eventDate(java.time.LocalDate.now().plusDays(15))
                    .eventTime("09:00 AM")
                    .venue(auditorium)
                    .maxSeats(500)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.PENDING)
                    .createdBy(acmLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 7. Entrepreneurship Talk (Pending Approval for Dean)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Entrepreneurship Talk")
                    .tagline("From Seed Idea to Venture Capital Funding")
                    .description("Founder fireside chats and angel investor pitch deck showcase.")
                    .category("Academic")
                    .department("Innovation & Entrepreneurship")
                    .eventDate(java.time.LocalDate.now().plusDays(18))
                    .eventTime("02:00 PM")
                    .venue(confCenter)
                    .maxSeats(200)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.PENDING)
                    .createdBy(ecellLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 8. Inter-College Football Championship (Pending Approval for Dean)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Inter-College Football Championship")
                    .tagline("MGM Annual Trophy Football Tournament")
                    .description("16 teams competing for the MGM rolling collegiate trophy.")
                    .category("Sports")
                    .department("Physical Education & Athletics")
                    .eventDate(java.time.LocalDate.now().plusDays(26))
                    .eventTime("03:30 PM")
                    .venue(sportsComplex)
                    .maxSeats(1200)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.PENDING)
                    .createdBy(sportsLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            // 9. Research Expo & Project Showcase (Pending Approval for Dean)
            eventRepository.save(com.college.eventmanagement.model.Event.builder()
                    .title("Research Expo & Project Showcase")
                    .tagline("Undergraduate & Faculty Research Paper Expo")
                    .description("Poster presentations, patent disclosures, and IEEE paper symposium.")
                    .category("Academic")
                    .department("R&D Cell")
                    .eventDate(java.time.LocalDate.now().plusDays(32))
                    .eventTime("11:00 AM")
                    .venue(confCenter)
                    .maxSeats(300)
                    .registeredCount(0)
                    .priceType("Free")
                    .price(0.0)
                    .status(com.college.eventmanagement.model.EventStatus.PENDING)
                    .createdBy(acmLead)
                    .createdAt(LocalDateTime.now())
                    .build());

            log.info("Initialized 9 club event proposals (Approved & Pending).");
        }
    }
}
