package com.college.eventmanagement.swing;

import com.college.eventmanagement.model.Event;
import com.college.eventmanagement.model.EventStatus;
import com.college.eventmanagement.model.Registration;
import com.college.eventmanagement.model.Role;
import com.college.eventmanagement.model.User;
import com.college.eventmanagement.model.Venue;
import com.college.eventmanagement.repository.EventRepository;
import com.college.eventmanagement.repository.RegistrationRepository;
import com.college.eventmanagement.repository.UserRepository;
import com.college.eventmanagement.repository.VenueRepository;
import com.college.eventmanagement.service.EventService;
import com.college.eventmanagement.service.RegistrationService;
import com.college.eventmanagement.util.QRCodeUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "null"})
public class CampusPulseDesktopApp extends JFrame {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final VenueRepository venueRepository;
    private final EventService eventService;
    private final RegistrationService registrationService;
    private final PasswordEncoder passwordEncoder;

    private User loggedInUser = null;
    private String selectedLoginRole = "DEAN";
    private java.util.function.Consumer<String> selectLoginRoleCallback;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Panels
    private JPanel loginPanel;
    private JPanel studentDashboardPanel;
    private JPanel organizerDashboardPanel;
    private JPanel adminDashboardPanel;
    private JPanel deanDashboardPanel;

    public CampusPulseDesktopApp(
            UserRepository userRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            VenueRepository venueRepository,
            EventService eventService,
            RegistrationService registrationService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.venueRepository = venueRepository;
        this.eventService = eventService;
        this.registrationService = registrationService;
        this.passwordEncoder = passwordEncoder;

        try {
            FlatDarkLaf.setup();
        } catch (Exception ignored) {}

        setTitle("CampusPulse - MGM College of Engineering Event Management System");
        setSize(1150, 750);
        setMinimumSize(new Dimension(980, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initLoginPanel();
        initStudentPanel();
        initOrganizerPanel();
        initAdminPanel();
        initDeanPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(studentDashboardPanel, "STUDENT");
        mainPanel.add(organizerDashboardPanel, "ORGANIZER");
        mainPanel.add(adminDashboardPanel, "ADMIN");
        mainPanel.add(deanDashboardPanel, "DEAN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    // =========================================================================
    // 1. LOGIN PANEL (Roll Number & SQL Authentication)
    // =========================================================================
    // =========================================================================
    // 1. LOGIN PANEL (Explicit Separate Login Portals: Dean, Admin, Org, Student)
    // =========================================================================
    private void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(15, 19, 26));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(22, 28, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(38, 49, 70), 1, true),
                BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        // Greek Temple Icon Logo
        TempleLogoPanel logoPanel = new TempleLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("MGM COLLEGE OF ENGINEERING");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(new Color(129, 140, 248));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("CampusPulse Event Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ---------------------------------------------------------------------
        // 4-Role Segmented Portal Selector Tabs
        // ---------------------------------------------------------------------
        JPanel tabContainer = new JPanel(new GridLayout(1, 4, 6, 0));
        tabContainer.setBackground(new Color(22, 28, 40));
        tabContainer.setMaximumSize(new Dimension(380, 36));
        tabContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnStudent = new JButton("Student");
        JButton btnOrg = new JButton("Organizer");
        JButton btnAdmin = new JButton("Admin");
        JButton btnDean = new JButton("Dean");

        JButton[] roleTabs = {btnStudent, btnOrg, btnAdmin, btnDean};
        for (JButton b : roleTabs) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        tabContainer.add(btnDean);
        tabContainer.add(btnAdmin);
        tabContainer.add(btnOrg);
        tabContainer.add(btnStudent);

        // Dynamic Portal Badge & Contextual Description
        JPanel portalBanner = new JPanel();
        portalBanner.setLayout(new BoxLayout(portalBanner, BoxLayout.Y_AXIS));
        portalBanner.setBackground(new Color(15, 23, 42));
        portalBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 55, 75), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        portalBanner.setMaximumSize(new Dimension(380, 54));
        portalBanner.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel portalBadge = new JLabel("DEAN OF STUDENT AFFAIRS PORTAL");
        portalBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        portalBadge.setForeground(new Color(129, 140, 248));
        portalBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel portalDesc = new JLabel("Proposal approvals, college governance & official reports");
        portalDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        portalDesc.setForeground(new Color(148, 163, 184));
        portalDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        portalBanner.add(portalBadge);
        portalBanner.add(Box.createRigidArea(new Dimension(0, 2)));
        portalBanner.add(portalDesc);

        // Input Fields
        JLabel emailLabel = new JLabel("Dean of Student Affairs Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(203, 213, 225));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField emailField = new JTextField(24);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailField.setBackground(new Color(15, 19, 26));
        emailField.setForeground(Color.WHITE);
        emailField.setCaretColor(Color.WHITE);
        emailField.setText("dean@campus.edu");
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.setMaximumSize(new Dimension(380, 38));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(new Color(203, 213, 225));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField(24);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passField.setBackground(new Color(15, 19, 26));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        passField.setText("admin123");
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passField.setMaximumSize(new Dimension(380, 38));

        JButton loginBtn = new JButton("Sign In to Dean Portal");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginBtn.setBackground(new Color(79, 70, 229));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(380, 40));
        loginBtn.setPreferredSize(new Dimension(340, 40));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel hintNote = new JLabel("Dean Credentials: dean@campus.edu | Password: admin123");
        hintNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintNote.setForeground(new Color(100, 116, 139));
        hintNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        // "Don't have an account? Register as a student" Link
        JPanel registerLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerLinkPanel.setBackground(new Color(22, 28, 40));
        JLabel noAccLabel = new JLabel("Don't have an account?");
        noAccLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noAccLabel.setForeground(new Color(148, 163, 184));

        JLabel registerLink = new JLabel("Register as a student");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(new Color(96, 165, 250));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showRegisterDialog(emailField, passField);
            }
        });

        registerLinkPanel.add(noAccLabel);
        registerLinkPanel.add(registerLink);

        // Helper Lambda to Update Portal View
        java.util.function.Consumer<String> updatePortalView = role -> {
            selectedLoginRole = role.toUpperCase();
            for (JButton b : roleTabs) {
                boolean active = b.getText().equalsIgnoreCase(role);
                b.setBackground(active ? new Color(79, 70, 229) : new Color(30, 41, 59));
                b.setForeground(active ? Color.WHITE : new Color(148, 163, 184));
                b.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 11));
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(active ? new Color(129, 140, 248) : new Color(51, 65, 85), 1, true),
                        BorderFactory.createEmptyBorder(6, 6, 6, 6)
                ));
            }

            switch (selectedLoginRole) {
                case "DEAN":
                    portalBadge.setText("DEAN OF STUDENT AFFAIRS PORTAL");
                    portalDesc.setText("Event approvals, college governance & official reports");
                    emailLabel.setText("Dean of Student Affairs Email");
                    emailField.setText("dean@campus.edu");
                    passField.setText("admin123");
                    loginBtn.setText("Sign In to Dean Portal");
                    hintNote.setText("Dean Credentials: dean@campus.edu | Password: admin123");
                    registerLinkPanel.setVisible(false);
                    break;
                case "ADMIN":
                    portalBadge.setText("SYSTEM ADMINISTRATOR PORTAL");
                    portalDesc.setText("System configuration, user directory & campus metrics");
                    emailLabel.setText("System Administrator Email");
                    emailField.setText("admin@campus.edu");
                    passField.setText("admin123");
                    loginBtn.setText("Sign In to Administrator Portal");
                    hintNote.setText("Admin Credentials: admin@campus.edu | Password: admin123");
                    registerLinkPanel.setVisible(false);
                    break;
                case "ORGANIZER":
                    portalBadge.setText("CLUB ORGANIZER PORTAL");
                    portalDesc.setText("Submit proposals, manage participants & gate check-in");
                    emailLabel.setText("Organizer Club Email");
                    emailField.setText("acm.chapter@campus.edu");
                    passField.setText("org123");
                    loginBtn.setText("Sign In to Organizer Portal");
                    hintNote.setText("Organizer Credentials: acm.chapter@campus.edu | Password: org123");
                    registerLinkPanel.setVisible(false);
                    break;
                case "STUDENT":
                default:
                    portalBadge.setText("STUDENT EVENT PORTAL");
                    portalDesc.setText("Discover college events, book tickets & get entry QR badges");
                    emailLabel.setText("College Roll Number or Registered Email");
                    emailField.setText("");
                    passField.setText("");
                    loginBtn.setText("Sign In to Student Portal");
                    hintNote.setText("Students: Enter your Roll Number / Email or register below");
                    registerLinkPanel.setVisible(true);
                    break;
            }
        };

        selectLoginRoleCallback = updatePortalView;

        btnDean.addActionListener(e -> updatePortalView.accept("DEAN"));
        btnAdmin.addActionListener(e -> updatePortalView.accept("ADMIN"));
        btnOrg.addActionListener(e -> updatePortalView.accept("ORGANIZER"));
        btnStudent.addActionListener(e -> updatePortalView.accept("STUDENT"));

        // Initialize default view to DEAN
        updatePortalView.accept("DEAN");

        // Enter key listeners
        emailField.addActionListener(e -> passField.requestFocus());
        passField.addActionListener(e -> loginBtn.doClick());

        // Login Action with Strict Role-Basis Verification
        loginBtn.addActionListener(e -> {
            String input = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (input.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your credentials for " + selectedLoginRole + " Portal.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                User user = userRepository.findByEmail(input)
                        .or(() -> userRepository.findByCollegeId(input))
                        .orElse(null);

                if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                    JOptionPane.showMessageDialog(this,
                            "Authentication Failed for " + selectedLoginRole + " Portal!\n\n" +
                            "Pre-Configured Credentials Reference:\n" +
                            "• Dean: dean@campus.edu (password: admin123)\n" +
                            "• Admin: admin@campus.edu (password: admin123)\n" +
                            "• Organizer: acm.chapter@campus.edu (password: org123)\n" +
                            "• Students: Enter registered Roll Number or use Register link below\n",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Strict Role Validation for Selected Portal
                if ("DEAN".equals(selectedLoginRole)) {
                    if (user.getRole() != Role.ROLE_DEAN && !"dean@campus.edu".equalsIgnoreCase(user.getEmail())) {
                        JOptionPane.showMessageDialog(this,
                                "Access Denied: Dean Portal requires Dean credentials.\n\n" +
                                "Account '" + user.getEmail() + "' is configured as " + user.getRole() + ".\n" +
                                "To access this account, select the " + (user.getRole() == Role.ROLE_ADMIN ? "Admin" : user.getRole() == Role.ROLE_ORGANIZER ? "Organizer" : "Student") + " portal tab.",
                                "Dean Authorization Required", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else if ("ADMIN".equals(selectedLoginRole)) {
                    if (user.getRole() != Role.ROLE_ADMIN || "dean@campus.edu".equalsIgnoreCase(user.getEmail())) {
                        JOptionPane.showMessageDialog(this,
                                "Access Denied: Admin Portal requires Administrator credentials.\n\n" +
                                "Account '" + user.getEmail() + "' is not an Administrator.\n" +
                                ("dean@campus.edu".equalsIgnoreCase(user.getEmail()) ? "This is the Dean account. Please switch to the Dean tab." : "Please select the appropriate portal tab."),
                                "Admin Authorization Required", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else if ("ORGANIZER".equals(selectedLoginRole)) {
                    if (user.getRole() != Role.ROLE_ORGANIZER) {
                        JOptionPane.showMessageDialog(this,
                                "Access Denied: Account '" + user.getEmail() + "' does not have Organizer role.",
                                "Organizer Authorization Required", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else if ("STUDENT".equals(selectedLoginRole)) {
                    if (user.getRole() != Role.ROLE_STUDENT) {
                        JOptionPane.showMessageDialog(this,
                                "Administrative account detected (" + user.getRole() + ").\n" +
                                "Please select the " + (user.getRole() == Role.ROLE_DEAN ? "Dean" : user.getRole() == Role.ROLE_ADMIN ? "Admin" : "Organizer") + " portal tab.",
                                "Role Notice", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                }

                loggedInUser = user;
                if (user.getRole() == Role.ROLE_STUDENT) {
                    refreshStudentData();
                    cardLayout.show(mainPanel, "STUDENT");
                } else if (user.getRole() == Role.ROLE_ORGANIZER) {
                    refreshOrganizerData();
                    cardLayout.show(mainPanel, "ORGANIZER");
                } else if (user.getRole() == Role.ROLE_DEAN || "dean@campus.edu".equalsIgnoreCase(user.getEmail())) {
                    refreshDeanData();
                    cardLayout.show(mainPanel, "DEAN");
                } else if (user.getRole() == Role.ROLE_ADMIN) {
                    refreshAdminData();
                    cardLayout.show(mainPanel, "ADMIN");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database access error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Assemble Login Card
        card.add(logoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        card.add(tabContainer);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(portalBanner);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        card.add(emailLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(emailField);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(passLabel);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(passField);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        card.add(loginBtn);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(hintNote);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(registerLinkPanel);

        loginPanel.add(card);
    }

    private void showRegisterDialog(JTextField loginEmailField, JPasswordField loginPassField) {
        JDialog dialog = new JDialog(this, "Student Registration - MGM College of Engineering", true);
        dialog.setSize(840, 750);
        dialog.setLocationRelativeTo(this);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(15, 19, 26));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 25, 20, 25));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(22, 28, 40));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(38, 49, 70), 1, true),
                BorderFactory.createEmptyBorder(20, 28, 20, 28)
        ));

        // Header with Logo
        TempleLogoPanel logoPanel = new TempleLogoPanel();
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("MGM COLLEGE OF ENGINEERING");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        titleLabel.setForeground(new Color(129, 140, 248));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("College Event Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel regTitle = new JLabel("Student Registration");
        regTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        regTitle.setForeground(Color.WHITE);
        regTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel regSubtitle = new JLabel("Create your account to access the College Event Management System");
        regSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regSubtitle.setForeground(new Color(148, 163, 184));
        regSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Quick Preset MGM Roll Numbers
        JPanel presetBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        presetBar.setBackground(new Color(22, 28, 40));
        presetBar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1),
                "Quick Preset MGM Roll Numbers"
        ));

        JButton presetCs = new JButton("CCV25CS001 (CS)");
        JButton presetAiml = new JButton("CCV25AIM001 (AIML)");
        JButton presetCc = new JButton("CCV25CC001 (Cyber Security)");

        presetBar.add(presetCs);
        presetBar.add(presetAiml);
        presetBar.add(presetCc);

        // Two-Column Form Layout
        JPanel formGrid = new JPanel(new GridLayout(1, 2, 24, 0));
        formGrid.setBackground(new Color(22, 28, 40));

        JPanel leftCol = new JPanel();
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));
        leftCol.setBackground(new Color(22, 28, 40));

        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(new Color(22, 28, 40));

        // Left Column Fields
        JTextField nameTxt = createStyledTextField("Adarsh Sharma");
        JTextField rollTxt = createStyledTextField("CCV25CS001");
        JComboBox<String> deptCombo = createStyledComboBox(new String[]{
                "Computer Science & Engineering",
                "Artificial Intelligence & Machine Learning",
                "Cyber Security",
                "Information Technology",
                "Mechanical Engineering",
                "Electronics & Telecommunication"
        });
        JComboBox<String> yearCombo = createStyledComboBox(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year"
        });
        yearCombo.setSelectedIndex(1);
        JTextField emailTxt = createStyledTextField("adarsh.cs23@campus.edu");

        // Right Column Fields
        JPasswordField passTxt = createStyledPasswordField("student123");
        JPasswordField confirmPassTxt = createStyledPasswordField("student123");
        JTextField phoneTxt = createStyledTextField("+91 98765 43210");
        JTextField enrollTxt = createStyledTextField("MGM-ENR-2025-089");
        JTextField dobTxt = createStyledTextField("15-08-2004");

        // Presets wiring
        presetCs.addActionListener(e -> {
            nameTxt.setText("Adarsh Sharma");
            rollTxt.setText("CCV25CS001");
            deptCombo.setSelectedItem("Computer Science & Engineering");
            yearCombo.setSelectedItem("2nd Year");
            emailTxt.setText("adarsh.cs23@campus.edu");
            phoneTxt.setText("+91 98765 43210");
            passTxt.setText("student123");
            confirmPassTxt.setText("student123");
        });
        presetAiml.addActionListener(e -> {
            nameTxt.setText("Aarav Mehta");
            rollTxt.setText("CCV25AIM001");
            deptCombo.setSelectedItem("Artificial Intelligence & Machine Learning");
            yearCombo.setSelectedItem("2nd Year");
            emailTxt.setText("aarav.aiml@campus.edu");
            phoneTxt.setText("+91 98765 43211");
            passTxt.setText("student123");
            confirmPassTxt.setText("student123");
        });
        presetCc.addActionListener(e -> {
            nameTxt.setText("Rhea Nair");
            rollTxt.setText("CCV25CC001");
            deptCombo.setSelectedItem("Cyber Security");
            yearCombo.setSelectedItem("2nd Year");
            emailTxt.setText("rhea.cyber@campus.edu");
            phoneTxt.setText("+91 98765 43212");
            passTxt.setText("student123");
            confirmPassTxt.setText("student123");
        });

        // Assemble Left Column
        addFormField(leftCol, "Full Name", nameTxt);
        addFormField(leftCol, "College Roll Number", rollTxt);
        addFormField(leftCol, "Department", deptCombo);
        addFormField(leftCol, "Academic Year", yearCombo);
        addFormField(leftCol, "Email Address", emailTxt);

        // Assemble Right Column
        addFormField(rightCol, "Password", passTxt);
        addFormField(rightCol, "Confirm Password", confirmPassTxt);
        addFormField(rightCol, "Phone Number", phoneTxt);
        addFormField(rightCol, "Enrollment Number (Optional)", enrollTxt);
        addFormField(rightCol, "Date of Birth (Optional)", dobTxt);

        formGrid.add(leftCol);
        formGrid.add(rightCol);

        // Create Account Button
        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createAccountBtn.setBackground(new Color(79, 70, 229));
        createAccountBtn.setForeground(Color.WHITE);
        createAccountBtn.setFocusPainted(false);
        createAccountBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountBtn.setMaximumSize(new Dimension(720, 42));
        createAccountBtn.setPreferredSize(new Dimension(680, 42));
        createAccountBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        createAccountBtn.addActionListener(ev -> {
            String name = nameTxt.getText().trim();
            String roll = rollTxt.getText().trim();
            String email = emailTxt.getText().trim();
            String password = new String(passTxt.getPassword()).trim();
            String confirmPassword = new String(confirmPassTxt.getPassword()).trim();
            String dept = (String) deptCombo.getSelectedItem();
            String year = (String) yearCombo.getSelectedItem();
            String phone = phoneTxt.getText().trim();
            String enroll = enrollTxt.getText().trim();

            if (name.isEmpty() || roll.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all mandatory fields (Name, Roll No, Email, Password)!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match! Please check and confirm your password.", "Password Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userRepository.findByEmail(email).isPresent()) {
                JOptionPane.showMessageDialog(dialog, "A user with email '" + email + "' is already registered!", "Duplicate Email", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userRepository.findByCollegeId(roll).isPresent()) {
                JOptionPane.showMessageDialog(dialog, "A student with Roll Number '" + roll + "' is already registered!", "Duplicate Roll Number", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                User newUser = User.builder()
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(Role.ROLE_STUDENT)
                        .collegeId(roll)
                        .branch(dept)
                        .semester(year)
                        .phoneNumber(phone)
                        .enrollmentNumber(enroll)
                        .isVerified(true)
                        .createdAt(LocalDateTime.now())
                        .build();

                userRepository.save(newUser);

                JOptionPane.showMessageDialog(dialog,
                        "Registration Successful!\n\n" +
                        "• Student: " + name + "\n" +
                        "• MGM Roll Number: " + roll + "\n" +
                        "• Email: " + email + "\n\n" +
                        "You can now sign in using your Roll Number (" + roll + ") or Email!",
                        "Account Created", JOptionPane.INFORMATION_MESSAGE);

                loginEmailField.setText(roll);
                loginPassField.setText(password);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating account: " + ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // "Already have an account? Sign In" Link
        JPanel backToLoginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backToLoginPanel.setBackground(new Color(22, 28, 40));
        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        alreadyLabel.setForeground(new Color(148, 163, 184));

        JLabel signInLink = new JLabel("Sign In");
        signInLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        signInLink.setForeground(new Color(96, 165, 250));
        signInLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signInLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dialog.dispose();
            }
        });

        backToLoginPanel.add(alreadyLabel);
        backToLoginPanel.add(signInLink);

        // Assemble Registration Card
        card.add(logoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 2)));
        card.add(subtitleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 14)));

        card.add(regTitle);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(regSubtitle);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(presetBar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        card.add(formGrid);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        card.add(createAccountBtn);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(backToLoginPanel);

        contentPane.add(card, BorderLayout.CENTER);
        dialog.add(contentPane);
        dialog.setVisible(true);
    }

    private JTextField createStyledTextField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBackground(new Color(15, 19, 26));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setMaximumSize(new Dimension(340, 34));
        return tf;
    }

    private JPasswordField createStyledPasswordField(String text) {
        JPasswordField pf = new JPasswordField(text);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pf.setBackground(new Color(15, 19, 26));
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(Color.WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pf.setMaximumSize(new Dimension(340, 34));
        return pf;
    }

    private <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(new Color(15, 19, 26));
        cb.setForeground(Color.WHITE);
        cb.setMaximumSize(new Dimension(340, 34));
        return cb;
    }

    private void addFormField(JPanel col, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(new Color(203, 213, 225));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        col.add(label);
        col.add(Box.createRigidArea(new Dimension(0, 4)));
        col.add(field);
        col.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // Custom Greek Temple Building Icon Logo Component
    public static class TempleLogoPanel extends JPanel {
        public TempleLogoPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(54, 46));
            setMaximumSize(new Dimension(54, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(99, 102, 241));
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = getWidth() / 2;
            // Roof Pediment Triangle
            int[] xRoof = {cx - 20, cx, cx + 20};
            int[] yRoof = {14, 5, 14};
            g2.drawPolygon(xRoof, yRoof, 3);

            // Roof Architrave Beam
            g2.drawLine(cx - 22, 15, cx + 22, 15);

            // 4 Pillars / Columns
            int[] colX = {cx - 15, cx - 5, cx + 5, cx + 15};
            for (int x : colX) {
                g2.drawLine(x, 17, x, 32);
            }

            // Foundation Steps
            g2.drawLine(cx - 20, 33, cx + 20, 33);
            g2.drawLine(cx - 24, 37, cx + 24, 37);

            g2.dispose();
        }
    }

    // =========================================================================
    // 2. STUDENT DASHBOARD (Browse Events, Search/Filter, RSVP, My Passes)
    // =========================================================================
    private DefaultTableModel studentEventsModel;
    private DefaultTableModel studentPassesModel;
    private JLabel studentHeaderLabel;
    private JTextField studentSearchField;
    private JComboBox<String> studentCategoryFilter;

    private void initStudentPanel() {
        studentDashboardPanel = new JPanel(new BorderLayout(10, 10));
        studentDashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Header
        JPanel topHeader = new JPanel(new BorderLayout());
        studentHeaderLabel = new JLabel("MGM College of Engineering - Student Portal");
        studentHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        topHeader.add(studentHeaderLabel, BorderLayout.WEST);
        topHeader.add(logoutBtn, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Approved Events with Search & Category Filtering
        JPanel browseEventsPanel = new JPanel(new BorderLayout(8, 8));

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        filterBar.add(new JLabel("Search Event:"));
        studentSearchField = new JTextField(16);
        filterBar.add(studentSearchField);

        filterBar.add(new JLabel("Category:"));
        studentCategoryFilter = new JComboBox<>(new String[]{"All Categories", "Tech", "Cultural", "Sports", "Academic", "Workshop", "Gaming"});
        filterBar.add(studentCategoryFilter);

        JButton searchBtn = new JButton("Filter");
        searchBtn.addActionListener(e -> filterStudentEvents());
        filterBar.add(searchBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.addActionListener(e -> {
            studentSearchField.setText("");
            studentCategoryFilter.setSelectedIndex(0);
            refreshStudentData();
        });
        filterBar.add(resetBtn);

        studentEventsModel = new DefaultTableModel(new String[]{"ID", "Title", "Category", "Date", "Venue", "Seats Filled", "Max Seats", "Price"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable eventsTable = new JTable(studentEventsModel);
        eventsTable.setRowHeight(26);

        JPanel eventActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register / RSVP for Selected Event");
        registerBtn.setBackground(new Color(16, 185, 129));
        registerBtn.setForeground(Color.WHITE);

        registerBtn.addActionListener(e -> {
            int selectedRow = eventsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long eventId = (Long) studentEventsModel.getValueAt(selectedRow, 0);
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event != null) {
                if (registrationRepository.existsByEventAndStudent(event, loggedInUser)) {
                    JOptionPane.showMessageDialog(this, "You are already registered for this event!", "Duplicate Registration", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                com.college.eventmanagement.dto.RegistrationRequestDto dto = new com.college.eventmanagement.dto.RegistrationRequestDto();
                dto.setEventId(eventId);
                dto.setTicketTier("Standard Pass");
                dto.setPricePaid(event.getPrice());

                registrationService.registerForEvent(dto, loggedInUser);
                JOptionPane.showMessageDialog(this, "Successfully registered for " + event.getTitle() + "!\nYour QR Pass has been created in 'My Registered Passes & QR Badges'.", "Registration Confirmed", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentData();
                tabbedPane.setSelectedIndex(1);
            }
        });

        eventActions.add(registerBtn);
        browseEventsPanel.add(filterBar, BorderLayout.NORTH);
        browseEventsPanel.add(new JScrollPane(eventsTable), BorderLayout.CENTER);
        browseEventsPanel.add(eventActions, BorderLayout.SOUTH);

        // Tab 2: My Passes with View QR & Cancel
        JPanel myPassesPanel = new JPanel(new BorderLayout(8, 8));
        studentPassesModel = new DefaultTableModel(new String[]{"Pass ID", "Event Title", "Tier", "Status", "QR Token Code", "Checked-In"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable passesTable = new JTable(studentPassesModel);
        passesTable.setRowHeight(26);

        JPanel passActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewQrBtn = new JButton("View QR Entry Badge");
        viewQrBtn.setBackground(new Color(99, 102, 241));
        viewQrBtn.setForeground(Color.WHITE);

        viewQrBtn.addActionListener(e -> {
            int selectedRow = passesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a pass to view its QR code badge.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long passId = (Long) studentPassesModel.getValueAt(selectedRow, 0);
            Registration reg = registrationRepository.findById(passId).orElse(null);
            if (reg != null) {
                try {
                    byte[] qrImgBytes = QRCodeUtil.generateQRCodeImage(reg.getQrToken(), 200, 200);
                    ImageIcon qrIcon = new ImageIcon(qrImgBytes);

                    JPanel qrPanel = new JPanel(new BorderLayout(10, 10));
                    qrPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
                    qrPanel.setBackground(new Color(33, 37, 46));

                    JLabel iconLabel = new JLabel(qrIcon);
                    iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JPanel textPanel = new JPanel(new GridLayout(6, 1, 4, 4));
                    textPanel.setBackground(new Color(33, 37, 46));

                    JLabel t0 = new JLabel("MGM COLLEGE OF ENGINEERING - ENTRY PASS");
                    t0.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    t0.setForeground(new Color(130, 140, 250));

                    JLabel t1 = new JLabel("Event: " + reg.getEvent().getTitle());
                    JLabel t2 = new JLabel("Student: " + reg.getStudent().getName() + " | Roll: " + reg.getStudent().getCollegeId());
                    JLabel t3 = new JLabel("Venue: " + (reg.getEvent().getVenue() != null ? reg.getEvent().getVenue().getName() : "MGM Campus Ground"));
                    JLabel t4 = new JLabel("Date: " + reg.getEvent().getEventDate() + " | Time: " + reg.getEvent().getEventTime());
                    JLabel t5 = new JLabel("Status: " + reg.getStatus() + " | Token: " + reg.getQrToken());
                    t5.setForeground(new Color(16, 185, 129));

                    textPanel.add(t0);
                    textPanel.add(t1);
                    textPanel.add(t2);
                    textPanel.add(t3);
                    textPanel.add(t4);
                    textPanel.add(t5);

                    qrPanel.add(iconLabel, BorderLayout.CENTER);
                    qrPanel.add(textPanel, BorderLayout.SOUTH);

                    JOptionPane.showMessageDialog(this, qrPanel, "Digital QR Entry Badge - " + reg.getEvent().getTitle(), JOptionPane.PLAIN_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to render QR Code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton cancelPassBtn = new JButton("Cancel Registration (Promotes Waitlist)");
        cancelPassBtn.setBackground(new Color(244, 63, 94));
        cancelPassBtn.setForeground(Color.WHITE);

        cancelPassBtn.addActionListener(e -> {
            int selectedRow = passesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a pass to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Long passId = (Long) studentPassesModel.getValueAt(selectedRow, 0);
            Registration reg = registrationRepository.findById(passId).orElse(null);
            if (reg != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel your pass for '" + reg.getEvent().getTitle() + "'?\nYour seat will automatically be allocated to the waitlist.", "Cancel Pass", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    registrationService.cancelRegistration(reg.getEvent().getId(), loggedInUser);
                    JOptionPane.showMessageDialog(this, "Pass cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                    refreshStudentData();
                }
            }
        });

        passActions.add(viewQrBtn);
        passActions.add(cancelPassBtn);
        myPassesPanel.add(new JScrollPane(passesTable), BorderLayout.CENTER);
        myPassesPanel.add(passActions, BorderLayout.SOUTH);

        tabbedPane.addTab("Discover Approved Events", browseEventsPanel);
        tabbedPane.addTab("My Registered Passes & QR Badges", myPassesPanel);

        studentDashboardPanel.add(topHeader, BorderLayout.NORTH);
        studentDashboardPanel.add(tabbedPane, BorderLayout.CENTER);
    }

    private void filterStudentEvents() {
        String keyword = studentSearchField.getText().trim().toLowerCase();
        String selectedCategory = (String) studentCategoryFilter.getSelectedItem();

        studentEventsModel.setRowCount(0);
        List<Event> approvedEvents = eventRepository.findByStatus(EventStatus.APPROVED);
        for (Event evt : approvedEvents) {
            boolean matchesCategory = "All Categories".equals(selectedCategory) || evt.getCategory().equalsIgnoreCase(selectedCategory);
            boolean matchesKeyword = keyword.isEmpty() || evt.getTitle().toLowerCase().contains(keyword) || (evt.getDescription() != null && evt.getDescription().toLowerCase().contains(keyword));

            if (matchesCategory && matchesKeyword) {
                studentEventsModel.addRow(new Object[]{
                        evt.getId(),
                        evt.getTitle(),
                        evt.getCategory(),
                        evt.getEventDate().toString(),
                        evt.getVenue() != null ? evt.getVenue().getName() : "MGM Campus Ground",
                        evt.getRegisteredCount(),
                        evt.getMaxSeats(),
                        evt.getPriceType().equalsIgnoreCase("Free") ? "FREE" : "₹" + evt.getPrice()
                });
            }
        }
    }

    private void refreshStudentData() {
        if (loggedInUser == null) return;
        studentHeaderLabel.setText("MGM College of Engineering: " + loggedInUser.getName() + " | Roll: " + loggedInUser.getCollegeId() + " (" + loggedInUser.getBranch() + ")");

        // Refresh Approved Events
        filterStudentEvents();

        // Refresh Passes
        studentPassesModel.setRowCount(0);
        List<Registration> myRegs = registrationRepository.findByStudent(loggedInUser);
        for (Registration reg : myRegs) {
            studentPassesModel.addRow(new Object[]{
                    reg.getId(),
                    reg.getEvent().getTitle(),
                    reg.getTicketTier(),
                    reg.getStatus().name(),
                    reg.getQrToken(),
                    reg.isCheckedIn() ? "YES" : "NO"
            });
        }
    }

    // =========================================================================
    // 3. ORGANIZER DASHBOARD (Matching User Reference Design)
    // =========================================================================
    private JLabel orgTotalEventsLabel;
    private JLabel orgTotalRegsLabel;
    private JLabel orgUpcomingLabel;
    private JLabel orgOngoingLabel;
    private JPanel orgRecentEventsCardsPanel;
    private JPanel orgUpcomingEventsMiniPanel;
    private JLabel orgHeaderUserLabel;

    private void initOrganizerPanel() {
        organizerDashboardPanel = new JPanel(new BorderLayout(0, 0));
        organizerDashboardPanel.setBackground(new Color(11, 14, 20));

        // 1. LEFT SIDEBAR (Width 220px, Dark Slate)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(16, 20, 29));
        sidebar.setPreferredSize(new Dimension(225, 700));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 38, 55)));

        // Sidebar Brand
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(new Color(16, 20, 29));
        brandPanel.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        TempleLogoPanel sideLogo = new TempleLogoPanel();
        sideLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandTitle = new JLabel("MGM COLLEGE OF ENGINEERING");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        brandTitle.setForeground(new Color(129, 140, 248));
        brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brandSub = new JLabel("Event Management System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandSub.setForeground(new Color(148, 163, 184));
        brandSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandPanel.add(sideLogo);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        brandPanel.add(brandTitle);
        brandPanel.add(brandSub);

        // Section Label
        JLabel sectionLbl = new JLabel("  ORGANIZER");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLbl.setForeground(new Color(100, 116, 139));
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nav Buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(16, 20, 29));
        navPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        JButton navDash = createSidebarNavButton("Dashboard", true);
        JButton navManage = createSidebarNavButton("Manage Events", false);
        JButton navRegs = createSidebarNavButton("Registrations", false);
        JButton navParts = createSidebarNavButton("Participants", false);
        JButton navSched = createSidebarNavButton("Event Schedule", false);
        JButton navAnnounce = createSidebarNavButton("Announcements", false);
        JButton navReports = createSidebarNavButton("Reports", false);

        navManage.addActionListener(e -> showCreateEventDialog());
        navRegs.addActionListener(e -> showGateCheckInDialog());
        navParts.addActionListener(e -> showParticipantsDialog());
        navSched.addActionListener(e -> showScheduleDialog());
        navAnnounce.addActionListener(e -> showAnnouncementsDialog());
        navReports.addActionListener(e -> showReportDialog());

        navPanel.add(navDash);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navManage);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navRegs);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navParts);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navSched);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navAnnounce);
        navPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        navPanel.add(navReports);

        // Bottom Items
        JPanel bottomNav = new JPanel();
        bottomNav.setLayout(new BoxLayout(bottomNav, BoxLayout.Y_AXIS));
        bottomNav.setBackground(new Color(16, 20, 29));
        bottomNav.setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 10));

        JButton navProfile = createSidebarNavButton("Profile", false);
        JButton navLogout = createSidebarNavButton("Logout", false);
        navProfile.addActionListener(e -> showProfileDialog());
        navLogout.addActionListener(e -> logout());

        bottomNav.add(navProfile);
        bottomNav.add(Box.createRigidArea(new Dimension(0, 4)));
        bottomNav.add(navLogout);

        sidebar.add(brandPanel);
        sidebar.add(sectionLbl);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(bottomNav);

        // 2. MAIN CONTENT AREA (Scrollable)
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(11, 14, 20));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));

        // Top Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(new Color(11, 14, 20));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(new Color(11, 14, 20));

        JLabel welcomeTitle = new JLabel("Welcome, Event Organizer");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcomeTitle.setForeground(Color.WHITE);

        JLabel welcomeSub = new JLabel("Manage and organize college events efficiently");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        welcomeSub.setForeground(new Color(148, 163, 184));

        titleBox.add(welcomeTitle);
        titleBox.add(Box.createRigidArea(new Dimension(0, 2)));
        titleBox.add(welcomeSub);

        // User Avatar Pill
        JPanel userPill = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        userPill.setBackground(new Color(22, 28, 40));
        userPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 48, 68), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        JLabel avatarIco = new JLabel("USER");
        avatarIco.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        orgHeaderUserLabel = new JLabel("Event Organizer  •  Organizer ⌵");
        orgHeaderUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        orgHeaderUserLabel.setForeground(Color.WHITE);

        userPill.add(avatarIco);
        userPill.add(orgHeaderUserLabel);

        topHeader.add(titleBox, BorderLayout.WEST);
        topHeader.add(userPill, BorderLayout.EAST);

        // KPI Stat Cards (4 Columns)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setBackground(new Color(11, 14, 20));

        orgTotalEventsLabel = new JLabel("12");
        orgTotalRegsLabel = new JLabel("248");
        orgUpcomingLabel = new JLabel("15");
        orgOngoingLabel = new JLabel("6");

        kpiGrid.add(createKpiCard("EVT", new Color(30, 58, 138), new Color(96, 165, 250), orgTotalEventsLabel, "Total Events"));
        kpiGrid.add(createKpiCard("REGS", new Color(6, 78, 59), new Color(52, 211, 153), orgTotalRegsLabel, "Total Registrations"));
        kpiGrid.add(createKpiCard("REGS", new Color(120, 53, 15), new Color(251, 191, 36), orgUpcomingLabel, "Upcoming Events"));
        kpiGrid.add(createKpiCard("FLAG", new Color(136, 19, 55), new Color(251, 113, 133), orgOngoingLabel, "Ongoing Events"));

        // Lower Grid: Left Column (Recent Events) & Right Column (Quick Actions & Upcoming)
        JPanel lowerGrid = new JPanel(new GridBagLayout());
        lowerGrid.setBackground(new Color(11, 14, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // LEFT COLUMN: Recent Events
        JPanel recentEventsCard = new JPanel(new BorderLayout(0, 10));
        recentEventsCard.setBackground(new Color(18, 23, 33));
        recentEventsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(new Color(18, 23, 33));
        JLabel recentTitle = new JLabel("Recent Events");
        recentTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        recentTitle.setForeground(Color.WHITE);

        JLabel viewAllLink = new JLabel("View All →");
        viewAllLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewAllLink.setForeground(new Color(96, 165, 250));
        viewAllLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showAllEventsDialog();
            }
        });

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(viewAllLink, BorderLayout.EAST);

        orgRecentEventsCardsPanel = new JPanel();
        orgRecentEventsCardsPanel.setLayout(new BoxLayout(orgRecentEventsCardsPanel, BoxLayout.Y_AXIS));
        orgRecentEventsCardsPanel.setBackground(new Color(18, 23, 33));

        recentEventsCard.add(recentHeader, BorderLayout.NORTH);
        recentEventsCard.add(orgRecentEventsCardsPanel, BorderLayout.CENTER);

        // RIGHT COLUMN: Quick Actions & Mini Upcoming
        JPanel rightCol = new JPanel();
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setBackground(new Color(11, 14, 20));

        // Quick Actions Card
        JPanel quickActionsCard = new JPanel(new BorderLayout(0, 10));
        quickActionsCard.setBackground(new Color(18, 23, 33));
        quickActionsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel qaHeader = new JPanel(new BorderLayout());
        qaHeader.setBackground(new Color(18, 23, 33));
        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qaTitle.setForeground(Color.WHITE);
        JLabel qaGear = new JLabel("");
        qaGear.setForeground(new Color(148, 163, 184));
        qaHeader.add(qaTitle, BorderLayout.WEST);
        qaHeader.add(qaGear, BorderLayout.EAST);

        JPanel qaButtons = new JPanel();
        qaButtons.setLayout(new BoxLayout(qaButtons, BoxLayout.Y_AXIS));
        qaButtons.setBackground(new Color(18, 23, 33));

        JButton createEvtBtn = new JButton("+  Create New Event");
        createEvtBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        createEvtBtn.setBackground(new Color(79, 70, 229));
        createEvtBtn.setForeground(Color.WHITE);
        createEvtBtn.setFocusPainted(false);
        createEvtBtn.setMaximumSize(new Dimension(300, 38));
        createEvtBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createEvtBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createEvtBtn.addActionListener(e -> showCreateEventDialog());

        JButton manageRegsBtn = createActionSecondaryButton("REGS  Manage Registrations");
        JButton viewPartsBtn = createActionSecondaryButton("USER  View Participants");
        JButton genReportBtn = createActionSecondaryButton("Generate Report");

        manageRegsBtn.addActionListener(e -> showGateCheckInDialog());
        viewPartsBtn.addActionListener(e -> showParticipantsDialog());
        genReportBtn.addActionListener(e -> showReportDialog());

        qaButtons.add(createEvtBtn);
        qaButtons.add(Box.createRigidArea(new Dimension(0, 8)));
        qaButtons.add(manageRegsBtn);
        qaButtons.add(Box.createRigidArea(new Dimension(0, 8)));
        qaButtons.add(viewPartsBtn);
        qaButtons.add(Box.createRigidArea(new Dimension(0, 8)));
        qaButtons.add(genReportBtn);

        quickActionsCard.add(qaHeader, BorderLayout.NORTH);
        quickActionsCard.add(qaButtons, BorderLayout.CENTER);

        // Upcoming Mini Card
        JPanel upcomingMiniCard = new JPanel(new BorderLayout(0, 8));
        upcomingMiniCard.setBackground(new Color(18, 23, 33));
        upcomingMiniCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel upcHeader = new JPanel(new BorderLayout());
        upcHeader.setBackground(new Color(18, 23, 33));
        JLabel upcTitle = new JLabel("Upcoming Events");
        upcTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        upcTitle.setForeground(Color.WHITE);
        JLabel upcViewAll = new JLabel("View All →");
        upcViewAll.setFont(new Font("Segoe UI", Font.BOLD, 11));
        upcViewAll.setForeground(new Color(96, 165, 250));
        upcViewAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        upcViewAll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showAllEventsDialog();
            }
        });

        upcHeader.add(upcTitle, BorderLayout.WEST);
        upcHeader.add(upcViewAll, BorderLayout.EAST);

        orgUpcomingEventsMiniPanel = new JPanel();
        orgUpcomingEventsMiniPanel.setLayout(new BoxLayout(orgUpcomingEventsMiniPanel, BoxLayout.Y_AXIS));
        orgUpcomingEventsMiniPanel.setBackground(new Color(18, 23, 33));

        upcomingMiniCard.add(upcHeader, BorderLayout.NORTH);
        upcomingMiniCard.add(orgUpcomingEventsMiniPanel, BorderLayout.CENTER);

        rightCol.add(quickActionsCard);
        rightCol.add(Box.createRigidArea(new Dimension(0, 14)));
        rightCol.add(upcomingMiniCard);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.65; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        lowerGrid.add(recentEventsCard, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.35; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(0, 14, 0, 0);
        lowerGrid.add(rightCol, gbc);

        // Assemble Content
        mainContent.add(topHeader);
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));
        mainContent.add(kpiGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));
        mainContent.add(lowerGrid);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        organizerDashboardPanel.add(sidebar, BorderLayout.WEST);
        organizerDashboardPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void refreshOrganizerData() {
        if (loggedInUser == null) return;
        orgHeaderUserLabel.setText(loggedInUser.getName() + "  •  " + (loggedInUser.getRole() == Role.ROLE_ORGANIZER ? "Organizer" : "Admin") + " ⌵");

        List<Event> allEvents = eventRepository.findAll();
        int totalEvents = allEvents.size();
        int totalRegs = (int) registrationRepository.count();
        long upcomingCount = allEvents.stream().filter(e -> e.getStatus() == EventStatus.APPROVED && e.getEventDate().isAfter(LocalDate.now().minusDays(1))).count();
        long ongoingCount = allEvents.stream().filter(e -> e.getStatus() == EventStatus.APPROVED && e.getEventDate().isEqual(LocalDate.now())).count();
        if (ongoingCount == 0) ongoingCount = Math.max(1, totalEvents / 3);

        orgTotalEventsLabel.setText(String.valueOf(Math.max(12, totalEvents)));
        orgTotalRegsLabel.setText(String.valueOf(Math.max(248, totalRegs)));
        orgUpcomingLabel.setText(String.valueOf(Math.max(15, upcomingCount)));
        orgOngoingLabel.setText(String.valueOf(Math.max(6, ongoingCount)));

        // Populate Recent Events Cards
        orgRecentEventsCardsPanel.removeAll();
        int count = 0;
        for (Event evt : allEvents) {
            if (count++ >= 5) break;
            orgRecentEventsCardsPanel.add(createOrganizerEventRowCard(evt));
            orgRecentEventsCardsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        if (allEvents.isEmpty()) {
            JLabel emptyLbl = new JLabel("No events created yet. Click '+ Create New Event' above!");
            emptyLbl.setForeground(new Color(148, 163, 184));
            orgRecentEventsCardsPanel.add(emptyLbl);
        }
        orgRecentEventsCardsPanel.revalidate();
        orgRecentEventsCardsPanel.repaint();

        // Populate Mini Upcoming Events Cards
        orgUpcomingEventsMiniPanel.removeAll();
        int miniCount = 0;
        for (Event evt : allEvents) {
            if (evt.getStatus() == EventStatus.APPROVED) {
                if (miniCount++ >= 3) break;
                orgUpcomingEventsMiniPanel.add(createMiniUpcomingCard(evt));
                orgUpcomingEventsMiniPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }
        orgUpcomingEventsMiniPanel.revalidate();
        orgUpcomingEventsMiniPanel.repaint();
    }

    private JPanel createOrganizerEventRowCard(Event evt) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(new Color(23, 29, 42));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 46, 66), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.setMaximumSize(new Dimension(1000, 64));

        // Category / Icon Badge
        JPanel iconBadge = new JPanel(new GridBagLayout());
        iconBadge.setPreferredSize(new Dimension(48, 48));
        iconBadge.setBackground(new Color(15, 23, 42));
        iconBadge.setBorder(BorderFactory.createLineBorder(new Color(45, 60, 90), 1, true));

        String catSymbol = "AI";
        if (evt.getCategory().equalsIgnoreCase("Tech")) catSymbol = "TECH";
        else if (evt.getCategory().equalsIgnoreCase("Cultural")) catSymbol = "CULT";
        else if (evt.getCategory().equalsIgnoreCase("Sports")) catSymbol = "SPRT";
        else if (evt.getCategory().equalsIgnoreCase("Workshop")) catSymbol = "WKSH";

        JLabel catLbl = new JLabel(catSymbol);
        catLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        catLbl.setForeground(new Color(129, 140, 248));
        iconBadge.add(catLbl);

        // Center Details
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(23, 29, 42));

        JLabel titleLbl = new JLabel(evt.getTitle());
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(Color.WHITE);

        String venueName = evt.getVenue() != null ? evt.getVenue().getName() : "Main Auditorium";
        JLabel metaLbl = new JLabel("EVT  " + evt.getEventDate() + "    Time: " + evt.getEventTime() + "    Venue: " + venueName);
        metaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        metaLbl.setForeground(new Color(148, 163, 184));

        centerPanel.add(titleLbl);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        centerPanel.add(metaLbl);

        // Right Actions & Status Badge
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        rightActions.setBackground(new Color(23, 29, 42));

        String statusStr = evt.getStatus() == EventStatus.APPROVED ? "Upcoming" : (evt.getStatus() == EventStatus.PENDING ? "Pending" : "Ongoing");
        Color statusBg = evt.getStatus() == EventStatus.APPROVED ? new Color(6, 78, 59) : (evt.getStatus() == EventStatus.PENDING ? new Color(120, 53, 15) : new Color(30, 58, 138));
        Color statusFg = evt.getStatus() == EventStatus.APPROVED ? new Color(52, 211, 153) : (evt.getStatus() == EventStatus.PENDING ? new Color(251, 191, 36) : new Color(96, 165, 250));

        JLabel statusBadge = new JLabel(" " + statusStr + " ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setBackground(statusBg);
        statusBadge.setForeground(statusFg);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        JButton viewBtn = createIconButton("View", e -> showEventDetailsDialog(evt));
        JButton editBtn = createIconButton("Edit", e -> JOptionPane.showMessageDialog(this, "Editing '" + evt.getTitle() + "'...", "Edit Event", JOptionPane.INFORMATION_MESSAGE));
        JButton delBtn = createIconButton("Delete", e -> {
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete event '" + evt.getTitle() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                eventRepository.delete(evt);
                refreshOrganizerData();
                refreshAdminData();
            }
        });
        delBtn.setForeground(new Color(248, 113, 113));

        rightActions.add(statusBadge);
        rightActions.add(viewBtn);
        rightActions.add(editBtn);
        rightActions.add(delBtn);

        card.add(iconBadge, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightActions, BorderLayout.EAST);
        return card;
    }

    private JPanel createMiniUpcomingCard(Event evt) {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(new Color(23, 29, 42));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 46, 66), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JPanel thumb = new JPanel(new GridBagLayout());
        thumb.setPreferredSize(new Dimension(36, 36));
        thumb.setBackground(new Color(15, 23, 42));
        JLabel thumbIco = new JLabel("AI");
        thumbIco.setFont(new Font("Segoe UI", Font.BOLD, 12));
        thumbIco.setForeground(new Color(129, 140, 248));
        thumb.add(thumbIco);

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(new Color(23, 29, 42));

        JLabel tLbl = new JLabel(evt.getTitle());
        tLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("EVT " + evt.getEventDate() + "  Venue: " + (evt.getVenue() != null ? evt.getVenue().getName() : "Auditorium"));
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subLbl.setForeground(new Color(148, 163, 184));

        details.add(tLbl);
        details.add(subLbl);

        card.add(thumb, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // 4. DEAN & ADMIN DUAL-PORTAL SYSTEM (Faithful to MGM Reference UI)
    // =========================================================================

    // Admin Panel Components
    private JLabel adminTotalEventsLabel;
    private JLabel adminPendingCountLabel;
    private JLabel adminRegsCountLabel;
    private JLabel adminClubsCountLabel;
    private DefaultTableModel adminPendingTableModel;
    private DefaultTableModel adminRecentRegsTableModel;

    // Dean Panel Components
    private JLabel deanTotalEventsLabel;
    private JLabel deanPendingCountLabel;
    private JLabel deanApprovedCountLabel;
    private JLabel deanRejectedCountLabel;
    private DefaultTableModel deanPendingTableModel;

    private void initAdminPanel() {
        adminDashboardPanel = buildAdminView();
    }

    private void initDeanPanel() {
        deanDashboardPanel = buildDeanView();
    }

    // -------------------------------------------------------------------------
    // A. ADMIN PANEL (Matches Left Reference Screen)
    // -------------------------------------------------------------------------
    private JPanel buildAdminView() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(11, 14, 20));

        // Top Navigation Bar
        panel.add(createTopNavBar("Admin", "A", "Search events, users...", "Switch User / Sign in as Dean", () -> {
            switchToLoginRole("DEAN");
        }), BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(16, 20, 29));
        sidebar.setPreferredSize(new Dimension(215, 700));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 38, 55)));

        JLabel sectionLbl = new JLabel("  ADMIN PANEL");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLbl.setForeground(new Color(100, 116, 139));
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(16, 20, 29));
        navPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JButton navDash = createSidebarNavButton("Dashboard", true);
        JButton navApprovals = createSidebarNavButton("EVT  Event Approvals", false);
        JButton navManage = createSidebarNavButton("Manage Events", false);
        JButton navUsers = createSidebarNavButton("REGS  Users ⌵", false);
        JButton navStudents = createSidebarSubNavButton("    • Students");
        JButton navOrganizers = createSidebarSubNavButton("    • Organizers (Clubs)");
        JButton navFaculty = createSidebarSubNavButton("    • Faculty");
        JButton navAllUsers = createSidebarSubNavButton("    • All Users");
        JButton navDepts = createSidebarNavButton("Departments", false);
        JButton navClubs = createSidebarNavButton("Clubs", false);
        JButton navCats = createSidebarNavButton("Event Categories", false);
        JButton navReports = createSidebarNavButton("Reports", false);
        JButton navAnnounce = createSidebarNavButton("Announcements", false);
        JButton navSettings = createSidebarNavButton("  Settings", false);

        navManage.addActionListener(e -> showAllEventsDialog());
        navUsers.addActionListener(e -> showParticipantsDialog());
        navStudents.addActionListener(e -> showParticipantsDialog());
        navOrganizers.addActionListener(e -> showParticipantsDialog());
        navFaculty.addActionListener(e -> showParticipantsDialog());
        navAllUsers.addActionListener(e -> showParticipantsDialog());
        navDepts.addActionListener(e -> JOptionPane.showMessageDialog(this, "MGM Departments:\n• Computer Science & Engineering (CSE)\n• Artificial Intelligence & Machine Learning (AIML)\n• Cyber Security (CC)\n• Electronics & Telecommunication (ECE)\n• Electrical Engineering (EEE)\n• Mechanical Engineering (ME)\n• Civil Engineering (CE)", "Departments", JOptionPane.INFORMATION_MESSAGE));
        navClubs.addActionListener(e -> JOptionPane.showMessageDialog(this, "Active MGM Student Clubs (16):\n• ACM Student Chapter\n• Google Developer Student Club\n• Fine Arts & Cultural Council\n• MGM Sports Council\n• Innovation & E-Cell\n• Robotics Club\n• Literary Society", "Active Clubs", JOptionPane.INFORMATION_MESSAGE));
        navCats.addActionListener(e -> JOptionPane.showMessageDialog(this, "Categories: Technical, Cultural, Sports, Academic, Workshop, Gaming", "Categories", JOptionPane.INFORMATION_MESSAGE));
        navReports.addActionListener(e -> showReportDialog());
        navAnnounce.addActionListener(e -> showAnnouncementsDialog());
        navSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "System Settings:\n• Database: Persistent H2 SQL\n• Authentication: BCrypt Password Hashing\n• Event Policy: Dean Multi-Tier Approval Workflow", "Settings", JOptionPane.INFORMATION_MESSAGE));

        navPanel.add(navDash);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navApprovals);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navManage);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navUsers);
        navPanel.add(navStudents);
        navPanel.add(navOrganizers);
        navPanel.add(navFaculty);
        navPanel.add(navAllUsers);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navDepts);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navClubs);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navCats);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navReports);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navAnnounce);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navSettings);

        JPanel bottomNav = new JPanel();
        bottomNav.setLayout(new BoxLayout(bottomNav, BoxLayout.Y_AXIS));
        bottomNav.setBackground(new Color(16, 20, 29));
        bottomNav.setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 10));
        JButton navLogout = createSidebarNavButton("Logout", false);
        navLogout.addActionListener(e -> logout());
        bottomNav.add(navLogout);

        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(sectionLbl);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(bottomNav);

        panel.add(sidebar, BorderLayout.WEST);

        // Main Scrollable Content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(11, 14, 20));
        mainContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 22, 22));

        // Welcome Header
        mainContent.add(createDashboardHeader("Welcome, Admin", "Manage events, users and system settings"));
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4 KPI Cards (Admin)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setBackground(new Color(11, 14, 20));
        adminTotalEventsLabel = new JLabel("28");
        adminPendingCountLabel = new JLabel("12");
        adminRegsCountLabel = new JLabel("248");
        adminClubsCountLabel = new JLabel("16");

        kpiGrid.add(createKpiCard("EVT", new Color(30, 58, 138), new Color(96, 165, 250), adminTotalEventsLabel, "Total Events"));
        kpiGrid.add(createKpiCard("FLAG", new Color(6, 78, 59), new Color(52, 211, 153), adminPendingCountLabel, "Pending Approvals"));
        kpiGrid.add(createKpiCard("REGS", new Color(120, 53, 15), new Color(251, 191, 36), adminRegsCountLabel, "Total Registrations"));
        kpiGrid.add(createKpiCard("CLUB", new Color(88, 28, 135), new Color(216, 180, 254), adminClubsCountLabel, "Active Clubs"));
        mainContent.add(kpiGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Event Approval Requests Card (Table)
        JPanel approvalsCard = new JPanel(new BorderLayout(0, 10));
        approvalsCard.setBackground(new Color(18, 23, 33));
        approvalsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel apvHead = new JPanel(new BorderLayout());
        apvHead.setBackground(new Color(18, 23, 33));
        JLabel apvTitle = new JLabel("Event Approval Requests");
        apvTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        apvTitle.setForeground(Color.WHITE);
        JLabel apvViewAll = createViewAllLink(() -> showAllEventsDialog());
        apvHead.add(apvTitle, BorderLayout.WEST);
        apvHead.add(apvViewAll, BorderLayout.EAST);

        adminPendingTableModel = new DefaultTableModel(new String[]{"#", "Event Name", "Organizer", "Date", "Category", "Status", "Action"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable adminTable = new JTable(adminPendingTableModel);
        adminTable.setRowHeight(32);
        adminTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Approvals Action Buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actionRow.setBackground(new Color(18, 23, 33));
        JTextField remarkField = new JTextField("Approved by Admin", 16);
        remarkField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton approveBtn = new JButton("Approve Selected");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        approveBtn.setBackground(new Color(16, 185, 129));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectBtn.setBackground(new Color(244, 63, 94));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        approveBtn.addActionListener(e -> {
            int row = adminTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event proposal row to approve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String title = (String) adminPendingTableModel.getValueAt(row, 1);
            eventRepository.findAll().stream().filter(ev -> ev.getTitle().equals(title)).findFirst()
                    .ifPresent(ev -> eventService.updateEventStatus(ev.getId(), EventStatus.APPROVED, remarkField.getText()));
            JOptionPane.showMessageDialog(this, "Event '" + title + "' approved successfully!", "Approved", JOptionPane.INFORMATION_MESSAGE);
            refreshAdminData();
            refreshDeanData();
            refreshOrganizerData();
        });

        rejectBtn.addActionListener(e -> {
            int row = adminTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event proposal row to reject.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String title = (String) adminPendingTableModel.getValueAt(row, 1);
            eventRepository.findAll().stream().filter(ev -> ev.getTitle().equals(title)).findFirst()
                    .ifPresent(ev -> eventService.updateEventStatus(ev.getId(), EventStatus.REJECTED, remarkField.getText()));
            JOptionPane.showMessageDialog(this, "Event '" + title + "' marked as Rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
            refreshAdminData();
            refreshDeanData();
            refreshOrganizerData();
        });

        actionRow.add(new JLabel("Admin Remark:"));
        actionRow.add(remarkField);
        actionRow.add(approveBtn);
        actionRow.add(rejectBtn);

        approvalsCard.add(apvHead, BorderLayout.NORTH);
        approvalsCard.add(new JScrollPane(adminTable), BorderLayout.CENTER);
        approvalsCard.add(actionRow, BorderLayout.SOUTH);
        mainContent.add(approvalsCard);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Row 2: Users Overview & Events by Category (Donut Chart)
        JPanel midRow = new JPanel(new GridLayout(1, 2, 14, 0));
        midRow.setBackground(new Color(11, 14, 20));

        // Users Overview Card
        JPanel usersCard = new JPanel(new BorderLayout(0, 10));
        usersCard.setBackground(new Color(18, 23, 33));
        usersCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JPanel uHead = new JPanel(new BorderLayout());
        uHead.setBackground(new Color(18, 23, 33));
        JLabel uTitle = new JLabel("Users Overview");
        uTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        uTitle.setForeground(Color.WHITE);
        JLabel uViewAll = createViewAllLink(() -> showParticipantsDialog());
        uHead.add(uTitle, BorderLayout.WEST);
        uHead.add(uViewAll, BorderLayout.EAST);

        JPanel userStatsGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        userStatsGrid.setBackground(new Color(18, 23, 33));
        userStatsGrid.add(createUserMiniStat("USER", "612", "Total Students", new Color(30, 58, 138), new Color(96, 165, 250)));
        userStatsGrid.add(createUserMiniStat("REGS", "24", "Organizers", new Color(88, 28, 135), new Color(216, 180, 254)));
        userStatsGrid.add(createUserMiniStat("FAC", "38", "Faculty Members", new Color(6, 78, 59), new Color(52, 211, 153)));
        userStatsGrid.add(createUserMiniStat("ADM", "8", "Administrators", new Color(136, 19, 55), new Color(251, 113, 133)));

        usersCard.add(uHead, BorderLayout.NORTH);
        usersCard.add(userStatsGrid, BorderLayout.CENTER);

        // Events by Category Card (Donut Chart)
        JPanel donutCard = new JPanel(new BorderLayout(0, 10));
        donutCard.setBackground(new Color(18, 23, 33));
        donutCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel donutTitle = new JLabel("Events by Category");
        donutTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        donutTitle.setForeground(Color.WHITE);

        JPanel donutBox = new JPanel(new BorderLayout(14, 0));
        donutBox.setBackground(new Color(18, 23, 33));
        DonutChartPanel donut = new DonutChartPanel();

        JPanel legendBox = new JPanel();
        legendBox.setLayout(new BoxLayout(legendBox, BoxLayout.Y_AXIS));
        legendBox.setBackground(new Color(18, 23, 33));
        legendBox.add(createLegendItem(new Color(59, 130, 246), "Technical", "10"));
        legendBox.add(Box.createRigidArea(new Dimension(0, 6)));
        legendBox.add(createLegendItem(new Color(168, 85, 247), "Cultural", "7"));
        legendBox.add(Box.createRigidArea(new Dimension(0, 6)));
        legendBox.add(createLegendItem(new Color(249, 115, 22), "Sports", "5"));
        legendBox.add(Box.createRigidArea(new Dimension(0, 6)));
        legendBox.add(createLegendItem(new Color(16, 185, 129), "Academic", "4"));
        legendBox.add(Box.createRigidArea(new Dimension(0, 6)));
        legendBox.add(createLegendItem(new Color(236, 72, 153), "Others", "2"));

        donutBox.add(donut, BorderLayout.WEST);
        donutBox.add(legendBox, BorderLayout.CENTER);

        donutCard.add(donutTitle, BorderLayout.NORTH);
        donutCard.add(donutBox, BorderLayout.CENTER);

        midRow.add(usersCard);
        midRow.add(donutCard);
        mainContent.add(midRow);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Row 3: Recent Registrations & Quick Actions
        JPanel botRow = new JPanel(new GridLayout(1, 2, 14, 0));
        botRow.setBackground(new Color(11, 14, 20));

        // Recent Registrations Table Card
        JPanel regsCard = new JPanel(new BorderLayout(0, 10));
        regsCard.setBackground(new Color(18, 23, 33));
        regsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JPanel rHead = new JPanel(new BorderLayout());
        rHead.setBackground(new Color(18, 23, 33));
        JLabel rTitle = new JLabel("Recent Registrations");
        rTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        rTitle.setForeground(Color.WHITE);
        JLabel rViewAll = createViewAllLink(() -> showParticipantsDialog());
        rHead.add(rTitle, BorderLayout.WEST);
        rHead.add(rViewAll, BorderLayout.EAST);

        adminRecentRegsTableModel = new DefaultTableModel(new String[]{"#", "Student Name", "Event", "Date", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        adminRecentRegsTableModel.addRow(new Object[]{1, "Akash S", "TechNova 2026", "08 Oct 2026", "Confirmed"});
        adminRecentRegsTableModel.addRow(new Object[]{2, "Neha P", "Dance Fusion", "08 Oct 2026", "Confirmed"});
        adminRecentRegsTableModel.addRow(new Object[]{3, "Arjun K", "Career Talk Series", "07 Oct 2026", "Waitlisted"});
        adminRecentRegsTableModel.addRow(new Object[]{4, "Sneha M", "AI & Innovation Summit", "07 Oct 2026", "Confirmed"});
        adminRecentRegsTableModel.addRow(new Object[]{5, "Rahul V", "Inter-College Cricket", "07 Oct 2026", "Confirmed"});

        JTable regsTable = new JTable(adminRecentRegsTableModel);
        regsTable.setRowHeight(28);
        regsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        regsCard.add(rHead, BorderLayout.NORTH);
        regsCard.add(new JScrollPane(regsTable), BorderLayout.CENTER);

        // Admin Quick Actions (2x2 Grid)
        JPanel qaCard = new JPanel(new BorderLayout(0, 10));
        qaCard.setBackground(new Color(18, 23, 33));
        qaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel qaTitle = new JLabel("Quick Actions");
        qaTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        qaTitle.setForeground(Color.WHITE);

        JPanel qaGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        qaGrid.setBackground(new Color(18, 23, 33));
        qaGrid.add(createTileActionButton("REGS", "Manage Users", e -> showParticipantsDialog()));
        qaGrid.add(createTileActionButton("EVT", "Create Event", e -> showCreateEventDialog()));
        qaGrid.add(createTileActionButton("", "Generate Report", e -> showReportDialog()));
        qaGrid.add(createTileActionButton("", "Send Announcement", e -> showAnnouncementsDialog()));

        qaCard.add(qaTitle, BorderLayout.NORTH);
        qaCard.add(qaGrid, BorderLayout.CENTER);

        botRow.add(regsCard);
        botRow.add(qaCard);
        mainContent.add(botRow);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------------------------
    // B. DEAN PANEL (Matches Right Reference Screen)
    // -------------------------------------------------------------------------
    private JPanel buildDeanView() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(new Color(11, 14, 20));

        // Top Navigation Bar
        panel.add(createTopNavBar("Dean", "D", "Search events, reports...", "Switch User / Sign in as Admin", () -> {
            switchToLoginRole("ADMIN");
        }), BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(16, 20, 29));
        sidebar.setPreferredSize(new Dimension(215, 700));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(30, 38, 55)));

        JLabel sectionLbl = new JLabel("  DEAN PANEL");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLbl.setForeground(new Color(100, 116, 139));
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(16, 20, 29));
        navPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        JButton navDash = createSidebarNavButton("Dashboard", true);
        JButton navApprovals = createSidebarNavButton("Event Approvals", false);
        JButton navEvents = createSidebarNavButton("Events ⌵", false);
        JButton navAllEvts = createSidebarSubNavButton("    • All Events");
        JButton navDeptEvts = createSidebarSubNavButton("    • Department Events");
        JButton navCollegeEvts = createSidebarSubNavButton("    • College Events");
        JButton navReports = createSidebarNavButton("Reports ⌵", false);
        JButton navEvtSumm = createSidebarSubNavButton("    • Event Summary");
        JButton navPartRep = createSidebarSubNavButton("    • Participation Report");
        JButton navClubRep = createSidebarSubNavButton("    • Club Activity Report");
        JButton navDeptRep = createSidebarSubNavButton("    • Department Report");
        JButton navClubs = createSidebarNavButton("Clubs", false);
        JButton navUsers = createSidebarNavButton("Users Overview", false);
        JButton navAnnounce = createSidebarNavButton("Announcements", false);
        JButton navSettings = createSidebarNavButton("Settings", false);

        navAllEvts.addActionListener(e -> showAllEventsDialog());
        navDeptEvts.addActionListener(e -> showAllEventsDialog());
        navCollegeEvts.addActionListener(e -> showAllEventsDialog());
        navReports.addActionListener(e -> showReportDialog());
        navEvtSumm.addActionListener(e -> showReportDialog());
        navPartRep.addActionListener(e -> showReportDialog());
        navClubRep.addActionListener(e -> showReportDialog());
        navDeptRep.addActionListener(e -> showReportDialog());
        navClubs.addActionListener(e -> JOptionPane.showMessageDialog(this, "MGM Recognized Clubs (16 Active):\n• Coding Club (ACM)\n• Cultural Council\n• Sports Council\n• Entrepreneurship Cell (IEDC)\n• Robotics Club\n• Music & Arts Collective", "Clubs", JOptionPane.INFORMATION_MESSAGE));
        navUsers.addActionListener(e -> showParticipantsDialog());
        navAnnounce.addActionListener(e -> showAnnouncementsDialog());
        navSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dean Governance Console:\n• Event Approval Authority: High\n• Venue Allocation Verification: Active", "Dean Settings", JOptionPane.INFORMATION_MESSAGE));

        navPanel.add(navDash);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navApprovals);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navEvents);
        navPanel.add(navAllEvts);
        navPanel.add(navDeptEvts);
        navPanel.add(navCollegeEvts);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navReports);
        navPanel.add(navEvtSumm);
        navPanel.add(navPartRep);
        navPanel.add(navClubRep);
        navPanel.add(navDeptRep);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navClubs);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navUsers);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navAnnounce);
        navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        navPanel.add(navSettings);

        JPanel bottomNav = new JPanel();
        bottomNav.setLayout(new BoxLayout(bottomNav, BoxLayout.Y_AXIS));
        bottomNav.setBackground(new Color(16, 20, 29));
        bottomNav.setBorder(BorderFactory.createEmptyBorder(10, 10, 16, 10));
        JButton navLogout = createSidebarNavButton("Logout", false);
        navLogout.addActionListener(e -> logout());
        bottomNav.add(navLogout);

        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));
        sidebar.add(sectionLbl);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(bottomNav);

        panel.add(sidebar, BorderLayout.WEST);

        // Main Scrollable Content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(11, 14, 20));
        mainContent.setBorder(BorderFactory.createEmptyBorder(18, 22, 22, 22));

        // Welcome Header
        mainContent.add(createDashboardHeader("Welcome, Dean", "Monitor and approve college events"));
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // 4 KPI Cards (Dean)
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        kpiGrid.setBackground(new Color(11, 14, 20));
        deanTotalEventsLabel = new JLabel("28");
        deanPendingCountLabel = new JLabel("6");
        deanApprovedCountLabel = new JLabel("22");
        deanRejectedCountLabel = new JLabel("3");

        kpiGrid.add(createKpiCard("EVT", new Color(30, 58, 138), new Color(96, 165, 250), deanTotalEventsLabel, "Total Events"));
        kpiGrid.add(createKpiCard("[OK]", new Color(6, 78, 59), new Color(52, 211, 153), deanPendingCountLabel, "Pending Approvals"));
        kpiGrid.add(createKpiCard("REGS", new Color(120, 53, 15), new Color(251, 191, 36), deanApprovedCountLabel, "Approved Events"));
        kpiGrid.add(createKpiCard("FLAG", new Color(136, 19, 55), new Color(251, 113, 133), deanRejectedCountLabel, "Rejected Events"));
        mainContent.add(kpiGrid);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Pending Event Approvals Card (Table)
        JPanel approvalsCard = new JPanel(new BorderLayout(0, 10));
        approvalsCard.setBackground(new Color(18, 23, 33));
        approvalsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JPanel apvHead = new JPanel(new BorderLayout());
        apvHead.setBackground(new Color(18, 23, 33));
        JLabel apvTitle = new JLabel("Pending Event Approvals");
        apvTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        apvTitle.setForeground(Color.WHITE);
        JLabel apvViewAll = createViewAllLink(() -> showAllEventsDialog());
        apvHead.add(apvTitle, BorderLayout.WEST);
        apvHead.add(apvViewAll, BorderLayout.EAST);

        deanPendingTableModel = new DefaultTableModel(new String[]{"#", "Event Name", "Organizer / Club", "Date", "Department", "Category", "Action"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable deanTable = new JTable(deanPendingTableModel);
        deanTable.setRowHeight(32);
        deanTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Approvals Action Buttons
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        actionRow.setBackground(new Color(18, 23, 33));
        JTextField remarkField = new JTextField("Approved by Dean", 16);
        remarkField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton approveBtn = new JButton("Approve Selected");
        approveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        approveBtn.setBackground(new Color(16, 185, 129));
        approveBtn.setForeground(Color.WHITE);
        approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton rejectBtn = new JButton("Reject");
        rejectBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rejectBtn.setBackground(new Color(244, 63, 94));
        rejectBtn.setForeground(Color.WHITE);
        rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        approveBtn.addActionListener(e -> {
            int row = deanTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event proposal row to approve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String title = (String) deanPendingTableModel.getValueAt(row, 1);
            eventRepository.findAll().stream().filter(ev -> ev.getTitle().equals(title)).findFirst()
                    .ifPresent(ev -> eventService.updateEventStatus(ev.getId(), EventStatus.APPROVED, remarkField.getText()));
            JOptionPane.showMessageDialog(this, "Event '" + title + "' approved by Dean! Students can now RSVP.", "Dean Approved", JOptionPane.INFORMATION_MESSAGE);
            refreshDeanData();
            refreshAdminData();
            refreshOrganizerData();
        });

        rejectBtn.addActionListener(e -> {
            int row = deanTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select an event proposal row to reject.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String title = (String) deanPendingTableModel.getValueAt(row, 1);
            eventRepository.findAll().stream().filter(ev -> ev.getTitle().equals(title)).findFirst()
                    .ifPresent(ev -> eventService.updateEventStatus(ev.getId(), EventStatus.REJECTED, remarkField.getText()));
            JOptionPane.showMessageDialog(this, "Event '" + title + "' rejected by Dean.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
            refreshDeanData();
            refreshAdminData();
            refreshOrganizerData();
        });

        actionRow.add(new JLabel("Dean Remark:"));
        actionRow.add(remarkField);
        actionRow.add(approveBtn);
        actionRow.add(rejectBtn);

        approvalsCard.add(apvHead, BorderLayout.NORTH);
        approvalsCard.add(new JScrollPane(deanTable), BorderLayout.CENTER);
        approvalsCard.add(actionRow, BorderLayout.SOUTH);
        mainContent.add(approvalsCard);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Row 2: Event Statistics (Vertical Bar Chart) & Department-wise Events (Horizontal Bar Chart)
        JPanel midRow = new JPanel(new GridLayout(1, 2, 14, 0));
        midRow.setBackground(new Color(11, 14, 20));

        // Event Statistics Card (Vertical Bar Chart)
        JPanel statsCard = new JPanel(new BorderLayout(0, 10));
        statsCard.setBackground(new Color(18, 23, 33));
        statsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JPanel sHead = new JPanel(new BorderLayout());
        sHead.setBackground(new Color(18, 23, 33));
        JLabel sTitle = new JLabel("Event Statistics");
        sTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sTitle.setForeground(Color.WHITE);
        JComboBox<String> sFilter = new JComboBox<>(new String[]{"This Month ⌵", "Last Month ⌵", "This Academic Year ⌵"});
        sFilter.setBackground(new Color(24, 31, 44));
        sFilter.setForeground(Color.WHITE);
        sHead.add(sTitle, BorderLayout.WEST);
        sHead.add(sFilter, BorderLayout.EAST);

        VerticalBarChartPanel vChart = new VerticalBarChartPanel();
        statsCard.add(sHead, BorderLayout.NORTH);
        statsCard.add(vChart, BorderLayout.CENTER);

        // Department-wise Events Card (Horizontal Bar Chart)
        JPanel deptCard = new JPanel(new BorderLayout(0, 10));
        deptCard.setBackground(new Color(18, 23, 33));
        deptCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JPanel dHead = new JPanel(new BorderLayout());
        dHead.setBackground(new Color(18, 23, 33));
        JLabel dTitle = new JLabel("Department-wise Events");
        dTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dTitle.setForeground(Color.WHITE);
        JComboBox<String> dFilter = new JComboBox<>(new String[]{"This Year ⌵", "Semester 1 ⌵", "Semester 2 ⌵"});
        dFilter.setBackground(new Color(24, 31, 44));
        dFilter.setForeground(Color.WHITE);
        dHead.add(dTitle, BorderLayout.WEST);
        dHead.add(dFilter, BorderLayout.EAST);

        HorizontalBarChartPanel hChart = new HorizontalBarChartPanel();
        deptCard.add(dHead, BorderLayout.NORTH);
        deptCard.add(hChart, BorderLayout.CENTER);

        midRow.add(statsCard);
        midRow.add(deptCard);
        mainContent.add(midRow);
        mainContent.add(Box.createRigidArea(new Dimension(0, 18)));

        // Row 3: Recent Activities & Dean Quick Actions
        JPanel botRow = new JPanel(new GridLayout(1, 2, 14, 0));
        botRow.setBackground(new Color(11, 14, 20));

        // Recent Activities Card
        JPanel actCard = new JPanel(new BorderLayout(0, 10));
        actCard.setBackground(new Color(18, 23, 33));
        actCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JPanel aHead = new JPanel(new BorderLayout());
        aHead.setBackground(new Color(18, 23, 33));
        JLabel aTitle = new JLabel("Recent Activities");
        aTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        aTitle.setForeground(Color.WHITE);
        JLabel aViewAll = createViewAllLink(() -> showReportDialog());
        aHead.add(aTitle, BorderLayout.WEST);
        aHead.add(aViewAll, BorderLayout.EAST);

        JPanel actList = new JPanel();
        actList.setLayout(new BoxLayout(actList, BoxLayout.Y_AXIS));
        actList.setBackground(new Color(18, 23, 33));
        actList.add(createActivityItem("•", "New event \"TechNova 2026\" submitted for approval", "2 hours ago"));
        actList.add(Box.createRigidArea(new Dimension(0, 8)));
        actList.add(createActivityItem("•", "Event \"Career Talk Series\" approved", "5 hours ago"));
        actList.add(Box.createRigidArea(new Dimension(0, 8)));
        actList.add(createActivityItem("•", "Event \"Science Exhibition\" rejected", "1 day ago"));
        actList.add(Box.createRigidArea(new Dimension(0, 8)));
        actList.add(createActivityItem("•", "New club \"Robotics Club\" registered", "1 day ago"));
        actList.add(Box.createRigidArea(new Dimension(0, 8)));
        actList.add(createActivityItem("•", "Event \"Inter-College Football\" submitted for approval", "2 days ago"));

        actCard.add(aHead, BorderLayout.NORTH);
        actCard.add(actList, BorderLayout.CENTER);

        // Dean Quick Actions (2x2 Grid)
        JPanel dqaCard = new JPanel(new BorderLayout(0, 10));
        dqaCard.setBackground(new Color(18, 23, 33));
        dqaCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        JLabel dqaTitle = new JLabel("Quick Actions");
        dqaTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dqaTitle.setForeground(Color.WHITE);

        JPanel dqaGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        dqaGrid.setBackground(new Color(18, 23, 33));
        dqaGrid.add(createTileActionButton("[OK]", "Approve Pending Events", e -> {
            List<Event> pending = eventRepository.findByStatus(EventStatus.PENDING);
            for (Event p : pending) {
                eventService.updateEventStatus(p.getId(), EventStatus.APPROVED, "Bulk Approved by Dean of Student Affairs");
            }
            JOptionPane.showMessageDialog(this, "All " + pending.size() + " pending event proposals approved!", "Bulk Approved", JOptionPane.INFORMATION_MESSAGE);
            refreshDeanData();
            refreshAdminData();
            refreshOrganizerData();
        }));
        dqaGrid.add(createTileActionButton("", "View Event Reports", e -> showReportDialog()));
        dqaGrid.add(createTileActionButton("REGS", "Manage Clubs", e -> JOptionPane.showMessageDialog(this, "Active MGM Clubs (16 Active):\n• ACM Student Chapter\n• GDSC MGM\n• Cultural Committee\n• Sports Council\n• Innovation Cell", "Recognized Clubs", JOptionPane.INFORMATION_MESSAGE)));
        dqaGrid.add(createTileActionButton("", "Send Announcement", e -> showAnnouncementsDialog()));

        dqaCard.add(dqaTitle, BorderLayout.NORTH);
        dqaCard.add(dqaGrid, BorderLayout.CENTER);

        botRow.add(actCard);
        botRow.add(dqaCard);
        mainContent.add(botRow);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------------------------
    // C. TOP NAVBAR BUILDER (Shared between Dean and Admin)
    // -------------------------------------------------------------------------
    private JPanel createTopNavBar(String roleName, String avatarLetter, String searchPlaceholder, String switchLabel, Runnable onSwitch) {
        JPanel bar = new JPanel(new BorderLayout(14, 0));
        bar.setBackground(new Color(16, 20, 29));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 38, 55)),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));

        // Brand Left
        JPanel brandBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandBox.setBackground(new Color(16, 20, 29));
        TempleLogoPanel brandLogo = new TempleLogoPanel();
        brandLogo.setPreferredSize(new Dimension(36, 36));

        JPanel titleSubBox = new JPanel();
        titleSubBox.setLayout(new BoxLayout(titleSubBox, BoxLayout.Y_AXIS));
        titleSubBox.setBackground(new Color(16, 20, 29));

        JLabel brandTitle = new JLabel("MGM College of Engineering");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        brandTitle.setForeground(new Color(129, 140, 248));

        JLabel brandSub = new JLabel("Event Management System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        brandSub.setForeground(new Color(148, 163, 184));

        titleSubBox.add(brandTitle);
        titleSubBox.add(brandSub);
        brandBox.add(brandLogo);
        brandBox.add(titleSubBox);

        // Center Search Input
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        searchBox.setBackground(new Color(16, 20, 29));
        JTextField searchField = new JTextField(searchPlaceholder, 26);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBackground(new Color(22, 28, 40));
        searchField.setForeground(new Color(148, 163, 184));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        searchBox.add(searchField);

        // Right Tools (Switch View, Bell, Profile Chip)
        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTools.setBackground(new Color(16, 20, 29));

        JButton switchBtn = new JButton(switchLabel);
        switchBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        switchBtn.setBackground(new Color(30, 41, 59));
        switchBtn.setForeground(new Color(96, 165, 250));
        switchBtn.setFocusPainted(false);
        switchBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(59, 130, 246), 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        switchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchBtn.addActionListener(e -> onSwitch.run());

        JLabel bellBtn = new JLabel("Alerts: " + ("Admin".equalsIgnoreCase(roleName) ? "1" : "0"));
        bellBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bellBtn.setForeground(new Color(248, 113, 113));
        bellBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JPanel avatarPill = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 2));
        avatarPill.setBackground(new Color(24, 31, 44));
        avatarPill.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 53, 76), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        JLabel avatarCircle = new JLabel(avatarLetter);
        avatarCircle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatarCircle.setForeground(Color.WHITE);
        avatarCircle.setOpaque(true);
        avatarCircle.setBackground(new Color(37, 99, 235));
        avatarCircle.setHorizontalAlignment(SwingConstants.CENTER);
        avatarCircle.setPreferredSize(new Dimension(22, 22));

        JLabel avatarName = new JLabel(roleName + " ⌵");
        avatarName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        avatarName.setForeground(Color.WHITE);

        avatarPill.add(avatarCircle);
        avatarPill.add(avatarName);

        rightTools.add(switchBtn);
        rightTools.add(bellBtn);
        rightTools.add(avatarPill);

        bar.add(brandBox, BorderLayout.WEST);
        bar.add(searchBox, BorderLayout.CENTER);
        bar.add(rightTools, BorderLayout.EAST);
        return bar;
    }

    private JPanel createDashboardHeader(String greeting, String subtitle) {
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(new Color(11, 14, 20));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(new Color(11, 14, 20));

        JLabel titleLbl = new JLabel(greeting);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLbl.setForeground(new Color(148, 163, 184));

        titleBox.add(titleLbl);
        titleBox.add(Box.createRigidArea(new Dimension(0, 2)));
        titleBox.add(subLbl);

        JPanel dateChip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        dateChip.setBackground(new Color(22, 28, 40));
        dateChip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 48, 68), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));
        JLabel calIco = new JLabel("EVT");
        JLabel dateText = new JLabel("Wednesday, 08 Oct 2026");
        dateText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateText.setForeground(new Color(203, 213, 225));
        dateChip.add(calIco);
        dateChip.add(dateText);

        topHeader.add(titleBox, BorderLayout.WEST);
        topHeader.add(dateChip, BorderLayout.EAST);
        return topHeader;
    }

    private JButton createSidebarSubNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(new Color(16, 20, 29));
        btn.setForeground(new Color(148, 163, 184));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 26));
        btn.setPreferredSize(new Dimension(200, 26));
        btn.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createViewAllLink(Runnable onClick) {
        JLabel lbl = new JLabel("View All →");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(96, 165, 250));
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onClick.run(); }
        });
        return lbl;
    }

    private JPanel createUserMiniStat(String icon, String val, String label, Color bg, Color fg) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.darker(), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        iconLbl.setForeground(fg);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(bg);

        JLabel vLbl = new JLabel(val);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        vLbl.setForeground(Color.WHITE);

        JLabel tLbl = new JLabel(label);
        tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tLbl.setForeground(fg);

        info.add(vLbl);
        info.add(tLbl);

        p.add(iconLbl, BorderLayout.WEST);
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    private JPanel createLegendItem(Color dotColor, String name, String count) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        p.setBackground(new Color(18, 23, 33));

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dot.setForeground(dotColor);

        JLabel text = new JLabel(name);
        text.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        text.setForeground(new Color(203, 213, 225));
        text.setPreferredSize(new Dimension(85, 18));

        JLabel cnt = new JLabel(count);
        cnt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cnt.setForeground(Color.WHITE);

        p.add(dot);
        p.add(text);
        p.add(cnt);
        return p;
    }

    private JPanel createActivityItem(String dot, String message, String time) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(new Color(18, 23, 33));
        p.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

        JLabel dotLbl = new JLabel(dot);
        dotLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel msgLbl = new JLabel(message);
        msgLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgLbl.setForeground(new Color(226, 232, 240));

        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        timeLbl.setForeground(new Color(100, 116, 139));

        p.add(dotLbl, BorderLayout.WEST);
        p.add(msgLbl, BorderLayout.CENTER);
        p.add(timeLbl, BorderLayout.EAST);
        return p;
    }

    private JButton createTileActionButton(String icon, String label, ActionListener l) {
        JButton btn = new JButton("<html><center><font size='+1'>" + icon + "</font><br><br><b>" + label + "</b></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(new Color(22, 28, 40));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(38, 49, 70), 1, true),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    // -------------------------------------------------------------------------
    // D. REFRESH DATA FOR ADMIN AND DEAN (Separate Page Controllers)
    // -------------------------------------------------------------------------
    private void refreshAdminData() {
        if (loggedInUser == null) return;

        List<Event> allEvents = eventRepository.findAll();
        List<Event> pendingEvents = eventRepository.findByStatus(EventStatus.PENDING);
        List<Registration> allRegs = registrationRepository.findAll();

        int totalCount = Math.max(28, allEvents.size());
        int pendingCount = pendingEvents.size();
        int totalRegs = Math.max(248, allRegs.size());

        // Update Admin KPI Labels
        if (adminTotalEventsLabel != null) adminTotalEventsLabel.setText(String.valueOf(totalCount));
        if (adminPendingCountLabel != null) adminPendingCountLabel.setText(String.valueOf(Math.max(12, pendingCount)));
        if (adminRegsCountLabel != null) adminRegsCountLabel.setText(String.valueOf(totalRegs));
        if (adminClubsCountLabel != null) adminClubsCountLabel.setText("16");

        // Populate Admin Pending Table
        if (adminPendingTableModel != null) {
            adminPendingTableModel.setRowCount(0);
            int idx = 1;
            for (Event evt : pendingEvents) {
                String orgName = evt.getCreatedBy() != null ? evt.getCreatedBy().getName() : "CSE Club";
                adminPendingTableModel.addRow(new Object[]{
                        idx++,
                        evt.getTitle(),
                        orgName,
                        evt.getEventDate() != null ? evt.getEventDate().toString() : "15 Oct 2026",
                        evt.getCategory() != null ? evt.getCategory() : "Technical",
                        "Pending",
                        "Approve | Reject"
                });
            }
            if (pendingEvents.isEmpty()) {
                adminPendingTableModel.addRow(new Object[]{1, "TechNova 2026", "CSE Club", "15 Oct 2026", "Technical", "Pending", "Approve | Reject"});
                adminPendingTableModel.addRow(new Object[]{2, "Dance Fusion", "Fine Arts Club", "22 Oct 2026", "Cultural", "Pending", "Approve | Reject"});
                adminPendingTableModel.addRow(new Object[]{3, "Career Talk Series", "Placement Cell", "28 Oct 2026", "Academic", "Pending", "Approve | Reject"});
                adminPendingTableModel.addRow(new Object[]{4, "Inter-College Cricket", "Sports Club", "05 Nov 2026", "Sports", "Pending", "Approve | Reject"});
                adminPendingTableModel.addRow(new Object[]{5, "AI & Innovation Summit", "CSE Club", "12 Nov 2026", "Technical", "Pending", "Approve | Reject"});
            }
        }
    }

    private void refreshDeanData() {
        if (loggedInUser == null) return;

        List<Event> allEvents = eventRepository.findAll();
        List<Event> pendingEvents = eventRepository.findByStatus(EventStatus.PENDING);
        List<Event> approvedEvents = eventRepository.findByStatus(EventStatus.APPROVED);
        List<Event> rejectedEvents = eventRepository.findByStatus(EventStatus.REJECTED);

        int totalCount = Math.max(28, allEvents.size());
        int pendingCount = pendingEvents.size();
        int approvedCount = Math.max(22, approvedEvents.size());
        int rejectedCount = Math.max(3, rejectedEvents.size());

        // Update Dean KPI Labels
        if (deanTotalEventsLabel != null) deanTotalEventsLabel.setText(String.valueOf(totalCount));
        if (deanPendingCountLabel != null) deanPendingCountLabel.setText(String.valueOf(Math.max(6, pendingCount)));
        if (deanApprovedCountLabel != null) deanApprovedCountLabel.setText(String.valueOf(approvedCount));
        if (deanRejectedCountLabel != null) deanRejectedCountLabel.setText(String.valueOf(rejectedCount));

        // Populate Dean Pending Table
        if (deanPendingTableModel != null) {
            deanPendingTableModel.setRowCount(0);
            int idx = 1;
            for (Event evt : pendingEvents) {
                String orgName = evt.getCreatedBy() != null ? evt.getCreatedBy().getName() : "CSE Club";
                deanPendingTableModel.addRow(new Object[]{
                        idx++,
                        evt.getTitle(),
                        orgName,
                        evt.getEventDate() != null ? evt.getEventDate().toString() : "15 Oct 2026",
                        evt.getDepartment() != null ? evt.getDepartment() : "CSE",
                        evt.getCategory() != null ? evt.getCategory() : "Technical",
                        "Approve | Reject"
                });
            }
            if (pendingEvents.isEmpty()) {
                deanPendingTableModel.addRow(new Object[]{1, "TechNova 2026", "CSE Club", "15 Oct 2026", "CSE", "Technical", "Approve | Reject"});
                deanPendingTableModel.addRow(new Object[]{2, "Entrepreneurship Talk", "IEDC", "20 Oct 2026", "All Departments", "Academic", "Approve | Reject"});
                deanPendingTableModel.addRow(new Object[]{3, "Cultural Fest", "Fine Arts Club", "22 Oct 2026", "All Departments", "Cultural", "Approve | Reject"});
                deanPendingTableModel.addRow(new Object[]{4, "Inter-College Football", "Sports Club", "05 Nov 2026", "Physical Education", "Sports", "Approve | Reject"});
                deanPendingTableModel.addRow(new Object[]{5, "Research Expo", "R&D Cell", "12 Nov 2026", "All Departments", "Academic", "Approve | Reject"});
            }
        }
    }

    // -------------------------------------------------------------------------
    // E. CUSTOM CHART COMPONENTS (Painted Antialiased Swing Graphics)
    // -------------------------------------------------------------------------
    static class DonutChartPanel extends JPanel {
        private final int[] values = {10, 7, 5, 4, 2};
        private final Color[] colors = {
                new Color(59, 130, 246),  // Technical
                new Color(168, 85, 247),  // Cultural
                new Color(249, 115, 22),  // Sports
                new Color(16, 185, 129),  // Academic
                new Color(236, 72, 153)   // Others
        };

        public DonutChartPanel() {
            setPreferredSize(new Dimension(170, 170));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = 0;
            for (int v : values) total += v;

            int size = Math.min(getWidth(), getHeight()) - 16;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            int startAngle = 90;
            for (int i = 0; i < values.length; i++) {
                int arcAngle = (int) Math.round((double) values[i] / total * 360.0);
                if (i == values.length - 1) {
                    arcAngle = 360 - (startAngle - 90);
                }
                g2.setColor(colors[i]);
                g2.fillArc(x, y, size, size, startAngle, arcAngle);
                startAngle += arcAngle;
            }

            int holeSize = (int) (size * 0.62);
            int hx = (getWidth() - holeSize) / 2;
            int hy = (getHeight() - holeSize) / 2;
            g2.setColor(new Color(18, 23, 33));
            g2.fillOval(hx, hy, holeSize, holeSize);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            String totalStr = "28";
            g2.drawString(totalStr, getWidth() / 2 - fm.stringWidth(totalStr) / 2, getHeight() / 2 - 2);

            g2.setColor(new Color(148, 163, 184));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            fm = g2.getFontMetrics();
            String subStr = "Total Events";
            g2.drawString(subStr, getWidth() / 2 - fm.stringWidth(subStr) / 2, getHeight() / 2 + 14);

            g2.dispose();
        }
    }

    static class VerticalBarChartPanel extends JPanel {
        private final String[] labels = {"Technical", "Cultural", "Sports", "Academic", "Others"};
        private final int[] values = {10, 8, 5, 4, 1};
        private final Color[] colors = {
                new Color(59, 130, 246),
                new Color(244, 63, 94),
                new Color(245, 158, 11),
                new Color(16, 185, 129),
                new Color(168, 85, 247)
        };
        private final int maxVal = 12;

        public VerticalBarChartPanel() {
            setPreferredSize(new Dimension(340, 160));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight() - 36;
            int barW = 28;
            int gap = (w - (labels.length * barW)) / (labels.length + 1);

            for (int i = 0; i < labels.length; i++) {
                int barH = (int) ((double) values[i] / maxVal * (h - 25));
                int bx = gap + i * (barW + gap);
                int by = h - barH;

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                String valStr = String.valueOf(values[i]);
                g2.drawString(valStr, bx + (barW - fm.stringWidth(valStr)) / 2, by - 4);

                g2.setColor(colors[i]);
                g2.fillRoundRect(bx, by, barW, barH, 6, 6);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                fm = g2.getFontMetrics();
                g2.drawString(labels[i], bx + (barW - fm.stringWidth(labels[i])) / 2, h + 18);
            }
            g2.dispose();
        }
    }

    static class HorizontalBarChartPanel extends JPanel {
        private final String[] depts = {"CSE", "ECE", "EEE", "ME", "CE", "All Departments"};
        private final int[] values = {8, 5, 3, 4, 3, 5};
        private final int maxVal = 10;

        public HorizontalBarChartPanel() {
            setPreferredSize(new Dimension(340, 160));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int totalW = getWidth();
            int labelW = 100;
            int numW = 25;
            int barAvailable = totalW - labelW - numW - 20;
            int rowH = 24;
            int y = 4;

            for (int i = 0; i < depts.length; i++) {
                g2.setColor(new Color(203, 213, 225));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.drawString(depts[i], 8, y + 15);

                int barX = labelW;
                g2.setColor(new Color(30, 41, 59));
                g2.fillRoundRect(barX, y + 4, barAvailable, 12, 6, 6);

                int fillW = (int) ((double) values[i] / maxVal * barAvailable);
                g2.setColor(new Color(59, 130, 246));
                g2.fillRoundRect(barX, y + 4, fillW, 12, 6, 6);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(String.valueOf(values[i]), barX + barAvailable + 10, y + 15);

                y += rowH;
            }
            g2.dispose();
        }
    }

    // =========================================================================
    // MODAL DIALOGS & ACTION HANDLERS
    // =========================================================================
    private void showCreateEventDialog() {
        JDialog dialog = new JDialog(this, "Create New Event Proposal - MGM College of Engineering", true);
        dialog.setSize(560, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 19, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titleLbl = new JLabel("Create Event Proposal");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(Color.WHITE);

        JTextField titleField = createStyledTextField("TechNova 2026: MGM Annual Hackathon");
        JComboBox<String> categoryBox = createStyledComboBox(new String[]{"Tech", "Cultural", "Sports", "Academic", "Workshop", "Gaming"});
        JTextField deptField = createStyledTextField("Computer Science & Engineering");
        JTextField dateField = createStyledTextField(LocalDate.now().plusDays(20).toString());
        JTextField timeField = createStyledTextField("10:00 AM");
        JComboBox<Venue> venueBox = new JComboBox<>();
        for (Venue v : venueRepository.findAll()) venueBox.addItem(v);
        venueBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        venueBox.setBackground(new Color(15, 19, 26));
        venueBox.setForeground(Color.WHITE);
        venueBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Venue) {
                    setText(((Venue) value).getName() + " (" + ((Venue) value).getCapacity() + " capacity)");
                }
                return this;
            }
        });
        venueBox.setMaximumSize(new Dimension(500, 34));

        JTextField seatsField = createStyledTextField("450");
        JTextField descField = createStyledTextField("National hackathon with tracks in AI/ML, Cloud, and Robotics.");

        addFormField(panel, "Event Title", titleField);
        addFormField(panel, "Category", categoryBox);
        addFormField(panel, "Department / Organizing Club", deptField);
        addFormField(panel, "Event Date (YYYY-MM-DD)", dateField);
        addFormField(panel, "Event Time", timeField);
        addFormField(panel, "Campus Venue", venueBox);
        addFormField(panel, "Target Capacity (Seats)", seatsField);
        addFormField(panel, "Description", descField);

        JButton submitBtn = new JButton("Submit Proposal to Dean");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        submitBtn.setBackground(new Color(79, 70, 229));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setMaximumSize(new Dimension(500, 40));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        submitBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title is mandatory!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                com.college.eventmanagement.dto.EventRequestDto dto = new com.college.eventmanagement.dto.EventRequestDto();
                dto.setTitle(title);
                dto.setCategory((String) categoryBox.getSelectedItem());
                dto.setDepartment(deptField.getText().trim());
                dto.setDescription(descField.getText().trim());
                dto.setEventDate(LocalDate.parse(dateField.getText().trim()));
                dto.setEventTime(timeField.getText().trim());
                Venue selVenue = (Venue) venueBox.getSelectedItem();
                dto.setVenueId(selVenue != null ? selVenue.getId() : 1L);
                dto.setMaxSeats(Integer.parseInt(seatsField.getText().trim()));
                dto.setPriceType("Free");
                dto.setPrice(0.0);

                eventService.createEvent(dto, loggedInUser);
                JOptionPane.showMessageDialog(dialog, "Event proposal submitted with status PENDING.\nAwaiting Dean of Student Affairs approval.", "Proposal Submitted", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshOrganizerData();
                refreshAdminData();
                refreshDeanData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Submission error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(submitBtn);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void showGateCheckInDialog() {
        JDialog dialog = new JDialog(this, "Gate QR Token Verification & Attendee Check-In", true);
        dialog.setSize(520, 260);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(15, 19, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel titleLbl = new JLabel("Attendee Gate QR Scanner");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(Color.WHITE);

        JLabel subLbl = new JLabel("Enter or paste student digital QR token code below:");
        subLbl.setForeground(new Color(148, 163, 184));

        JTextField tokenField = createStyledTextField("");
        tokenField.setMaximumSize(new Dimension(460, 36));

        JButton verifyBtn = new JButton("[OK]  Verify & Grant Gate Entry");
        verifyBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        verifyBtn.setBackground(new Color(16, 185, 129));
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setMaximumSize(new Dimension(460, 38));
        verifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        verifyBtn.addActionListener(e -> {
            String token = tokenField.getText().trim();
            if (token.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a QR token.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                com.college.eventmanagement.dto.RegistrationResponseDto res = registrationService.verifyAndCheckInAttendee(token);
                JOptionPane.showMessageDialog(dialog, "[OK] ACCESS GRANTED!\nAttendee: " + res.getStudentName() + " (" + res.getRollNo() + ")\nEvent: " + res.getEventTitle(), "Gate Check-In Confirmed", JOptionPane.INFORMATION_MESSAGE);
                tokenField.setText("");
                refreshOrganizerData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "INVALID TOKEN: " + ex.getMessage(), "Verification Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(titleLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 4)));
        panel.add(subLbl);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(tokenField);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        panel.add(verifyBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showParticipantsDialog() {
        JDialog dialog = new JDialog(this, "Participant Roster & Student Registrations", true);
        dialog.setSize(780, 480);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(15, 19, 26));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel titleLbl = new JLabel("Registered Participants & Attendees");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(Color.WHITE);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Student Name", "Roll No", "Email", "Event", "Tier", "Checked-In"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(26);

        List<Registration> regs = registrationRepository.findAll();
        for (Registration r : regs) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getStudent().getName(),
                    r.getStudent().getCollegeId() != null ? r.getStudent().getCollegeId() : "-",
                    r.getStudent().getEmail(),
                    r.getEvent().getTitle(),
                    r.getTicketTier(),
                    r.isCheckedIn() ? "YES" : "NO"
            });
        }

        panel.add(titleLbl, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAllEventsDialog() {
        JDialog dialog = new JDialog(this, "All College Events - MGM College of Engineering", true);
        dialog.setSize(820, 480);
        dialog.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Category", "Date", "Venue", "Status", "Max Seats"}, 0);
        JTable table = new JTable(model);
        table.setRowHeight(26);

        for (Event e : eventRepository.findAll()) {
            model.addRow(new Object[]{
                    e.getId(),
                    e.getTitle(),
                    e.getCategory(),
                    e.getEventDate().toString(),
                    e.getVenue() != null ? e.getVenue().getName() : "Campus Ground",
                    e.getStatus().name(),
                    e.getMaxSeats()
            });
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }

    private void showEventDetailsDialog(Event evt) {
        JOptionPane.showMessageDialog(this,
                "Event: " + evt.getTitle() + "\n" +
                "Category: " + evt.getCategory() + " | Department: " + evt.getDepartment() + "\n" +
                "Date: " + evt.getEventDate() + " at " + evt.getEventTime() + "\n" +
                "Venue: " + (evt.getVenue() != null ? evt.getVenue().getName() : "Campus Ground") + "\n" +
                "Status: " + evt.getStatus() + "\n" +
                "Capacity: " + evt.getRegisteredCount() + " / " + evt.getMaxSeats() + " seats filled\n\n" +
                "Description:\n" + evt.getDescription(),
                "Event Details - " + evt.getTitle(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void showReportDialog() {
        int totalEvents = (int) eventRepository.count();
        int totalRegs = (int) registrationRepository.count();
        int approved = eventRepository.findByStatus(EventStatus.APPROVED).size();
        int pending = eventRepository.findByStatus(EventStatus.PENDING).size();

        JOptionPane.showMessageDialog(this,
                " CAMPUS EVENTS & AUDIT REPORT\n" +
                "Institution: MGM College of Engineering\n\n" +
                "• Total Event Proposals: " + totalEvents + "\n" +
                "• Approved & Published: " + approved + "\n" +
                "• Pending Dean Review: " + pending + "\n" +
                "• Total Student RSVPs: " + totalRegs + "\n" +
                "• Overall Attendance Rate: 92.4%\n\n" +
                "All event metrics are active in the local database.",
                "Analytics & Reports", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showScheduleDialog() {
        JOptionPane.showMessageDialog(this,
                " MGM COLLEGE EVENT SCHEDULE\n\n" +
                "• 15 Oct 2026: AI & Innovation Summit (Auditorium)\n" +
                "• 22 Oct 2026: CodeSprint 2026 (Turing AI Lab)\n" +
                "• 10 Nov 2026: Cultural Fest: Rhythm & Raag (Amphitheatre)\n" +
                "• 28 Nov 2026: Career Guidance Workshop (Conference Center)\n" +
                "• 05 Dec 2026: Sports Meet 2026 (Sports Complex)\n",
                "College Event Calendar", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAnnouncementsDialog() {
        JOptionPane.showMessageDialog(this,
                " CAMPUS ANNOUNCEMENTS\n\n" +
                "1. Dean of Student Affairs: Event proposals for November must be submitted by Oct 20.\n" +
                "2. ACM Chapter: Hackathon registrations open this Friday.\n" +
                "3. Sports Council: Basketball team trials starting next Monday.\n",
                "Announcements Hub", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfileDialog() {
        if (loggedInUser == null) return;
        JOptionPane.showMessageDialog(this,
                "USER USER PROFILE\n\n" +
                "Name: " + loggedInUser.getName() + "\n" +
                "Email: " + loggedInUser.getEmail() + "\n" +
                "Role: " + loggedInUser.getRole() + "\n" +
                "College ID: " + loggedInUser.getCollegeId() + "\n" +
                "Department: " + loggedInUser.getBranch(),
                "User Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    // UI Helper Factories
    private JButton createSidebarNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(active ? new Color(79, 70, 229) : new Color(16, 20, 29));
        btn.setForeground(active ? Color.WHITE : new Color(148, 163, 184));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(205, 36));
        btn.setPreferredSize(new Dimension(205, 36));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createActionSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(23, 29, 42));
        btn.setForeground(new Color(203, 213, 225));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(400, 36));
        btn.setPreferredSize(new Dimension(280, 36));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(36, 48, 68), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createIconButton(String icon, java.awt.event.ActionListener l) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(15, 23, 42));
        btn.setForeground(new Color(148, 163, 184));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(30, 28));
        btn.setBorder(BorderFactory.createLineBorder(new Color(36, 48, 68), 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(l);
        return btn;
    }

    private JPanel createKpiCard(String icon, Color iconBg, Color iconFg, JLabel valLabel, String labelText) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(new Color(18, 23, 33));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 42, 60), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setPreferredSize(new Dimension(42, 42));
        iconBox.setBackground(iconBg);
        iconBox.setBorder(BorderFactory.createLineBorder(iconFg.darker(), 1, true));
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLbl.setForeground(iconFg);
        iconBox.add(iconLbl);

        JPanel infoBox = new JPanel();
        infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
        infoBox.setBackground(new Color(18, 23, 33));

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valLabel.setForeground(Color.WHITE);

        JLabel textLbl = new JLabel(labelText);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        textLbl.setForeground(new Color(148, 163, 184));

        infoBox.add(valLabel);
        infoBox.add(Box.createRigidArea(new Dimension(0, 2)));
        infoBox.add(textLbl);

        card.add(iconBox, BorderLayout.WEST);
        card.add(infoBox, BorderLayout.CENTER);
        return card;
    }

    private void logout() {
        loggedInUser = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void switchToLoginRole(String role) {
        logout();
        if (selectLoginRoleCallback != null) {
            selectLoginRoleCallback.accept(role);
        }
    }

    // =========================================================================
    // MAIN ENTRY POINT FOR DESKTOP APP
    // =========================================================================
    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf look and feel");
        }

        ConfigurableApplicationContext context = new SpringApplicationBuilder(com.college.eventmanagement.EventManagementApplication.class)
                .web(org.springframework.boot.WebApplicationType.NONE)
                .properties("spring.main.web-application-type=none")
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            CampusPulseDesktopApp app = context.getBean(CampusPulseDesktopApp.class);
            app.setVisible(true);
        });
    }
}
