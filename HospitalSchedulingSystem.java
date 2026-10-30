
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HospitalSchedulingSystem extends JFrame {

    static class Patient {
        int id;
        String name, department, bed;
        int priority;

        Patient(int id, String name, String department, int priority) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.priority = priority;
            this.bed = "Not Allocated";
        }
    }

    static class Appointment {
        int patientId;
        String patientName, department, date, time;

        Appointment(int id, String name, String department,
                    String date, String time) {
            this.patientId = id;
            this.patientName = name;
            this.department = department;
            this.date = date;
            this.time = time;
        }
    }

    private final ArrayList<Patient> patients = new ArrayList<>();
    private final ArrayList<Appointment> appointments = new ArrayList<>();

    private final String[] beds = {
        "ICU-01", "ICU-02",
        "GEN-01", "GEN-02", "GEN-03",
        "EMR-01", "EMR-02"
    };

    private final DefaultTableModel patientModel =
        new DefaultTableModel(
            new String[]{"Patient ID", "Name", "Department",
                         "Priority", "Assigned Bed"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final DefaultTableModel appointmentModel =
        new DefaultTableModel(
            new String[]{"Patient ID", "Patient Name", "Department",
                         "Date", "Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable patientTable = new JTable(patientModel);
    private final JTable appointmentTable = new JTable(appointmentModel);

    private final JLabel totalLabel = new JLabel();
    private final JLabel allocatedLabel = new JLabel();
    private final JLabel availableLabel = new JLabel();

    private final Color blue = new Color(34, 94, 160);
    private final Color background = new Color(245, 248, 252);

    public HospitalSchedulingSystem() {
        setTitle("Hospital Bed Allocation and Patient Scheduling System");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(background);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        refreshTables();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(blue);
        header.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel title = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel subtitle = new JLabel(
            "Bed Allocation | Patient Priority | Appointment Scheduling");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel texts = new JPanel(new GridLayout(2, 1, 0, 5));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(subtitle);

        header.add(texts, BorderLayout.WEST);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 12));
        main.setBackground(background);
        main.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel dashboard = new JPanel(new GridLayout(1, 3, 12, 0));
        dashboard.setBackground(background);
        dashboard.add(createCard("Total Patients", totalLabel));
        dashboard.add(createCard("Allocated Beds", allocatedLabel));
        dashboard.add(createCard("Available Beds", availableLabel));

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setBackground(background);
        top.add(dashboard, BorderLayout.NORTH);
        top.add(createButtons(), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patient Records", createTablePanel(patientTable));
        tabs.addTab("Appointments", createTablePanel(appointmentTable));

        main.add(top, BorderLayout.NORTH);
        main.add(tabs, BorderLayout.CENTER);

        return main;
    }

    private JPanel createCard(String heading, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 228, 238)),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        JLabel label = new JLabel(heading);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));

        value.setFont(new Font("SansSerif", Font.BOLD, 23));
        value.setForeground(blue);

        card.add(label);
        card.add(value);
        return card;
    }

    private JPanel createButtons() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));

        panel.add(createButton("Add Patient", this::addPatient));
        panel.add(createButton("Allocate Beds", this::allocateBeds));
        panel.add(createButton("Sort by Priority", this::sortPatients));
        panel.add(createButton("Search Patient", this::searchPatient));
        panel.add(createButton("Schedule Appointment",
                               this::scheduleAppointment));
        panel.add(createButton("Refresh Tables", this::refreshTables));

        return panel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBackground(blue);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JPanel createTablePanel(JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 12));
        table.setAutoCreateRowSorter(true);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(new Color(230, 237, 246));

        JLabel label = new JLabel(
            "Academic DAA Project | Hospital Scheduling Prototype");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        footer.add(label);
        return footer;
    }

    private void addPatient() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();

        JComboBox<String> departmentBox = new JComboBox<>(
            new String[]{"ICU", "GENERAL", "EMERGENCY"});

        JComboBox<Integer> priorityBox = new JComboBox<>(
            new Integer[]{1, 2, 3, 4, 5});

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 10));
        form.add(new JLabel("Patient ID:"));
        form.add(idField);
        form.add(new JLabel("Patient Name:"));
        form.add(nameField);
        form.add(new JLabel("Department:"));
        form.add(departmentBox);
        form.add(new JLabel("Priority (1-5):"));
        form.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Register New Patient",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            String name = nameField.getText().trim();
            String department = (String) departmentBox.getSelectedItem();
            int priority = (Integer) priorityBox.getSelectedItem();

            if (id <= 0 || name.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, "Enter a valid ID and patient name.");
                return;
            }

            for (Patient p : patients) {
                if (p.id == id) {
                    JOptionPane.showMessageDialog(
                        this, "This patient ID already exists.");
                    return;
                }
            }

            patients.add(new Patient(id, name, department, priority));
            refreshTables();

            JOptionPane.showMessageDialog(
                this, "Patient registered successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Patient ID must be a valid number.");
        }
    }

    private void allocateBeds() {
        Set<String> occupied = new HashSet<>();

        for (Patient p : patients) {
            if (!p.bed.equals("Not Allocated")) {
                occupied.add(p.bed);
            }
        }

        ArrayList<Patient> pending = new ArrayList<>();

        for (Patient p : patients) {
            if (p.bed.equals("Not Allocated")) {
                pending.add(p);
            }
        }

        pending.sort(Comparator.comparingInt(
            (Patient p) -> p.priority).reversed()
            .thenComparingInt(p -> p.id));

        StringBuilder report = new StringBuilder();

        for (Patient p : pending) {
            String prefix;

            switch (p.department) {
                case "ICU":
                    prefix = "ICU";
                    break;
                case "EMERGENCY":
                    prefix = "EMR";
                    break;
                default:
                    prefix = "GEN";
            }

            String selected = null;

            for (String bed : beds) {
                if (bed.startsWith(prefix) && !occupied.contains(bed)) {
                    selected = bed;
                    break;
                }
            }

            if (selected != null) {
                p.bed = selected;
                occupied.add(selected);
                report.append(p.name).append(" -> ")
                      .append(selected).append("\n");
            } else {
                report.append(p.name)
                      .append(" -> No suitable bed available\n");
            }
        }

        refreshTables();

        if (pending.isEmpty()) {
            report.append("No unallocated patients.");
        }

        JOptionPane.showMessageDialog(
            this, report.toString(), "Bed Allocation Results",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void sortPatients() {
        patients.sort(Comparator.comparingInt(
            (Patient p) -> p.priority).reversed()
            .thenComparingInt(p -> p.id));

        refreshTables();

        JOptionPane.showMessageDialog(
            this, "Patients sorted by priority (highest first).");
    }

    private void searchPatient() {
        String input = JOptionPane.showInputDialog(
            this, "Enter Patient ID:", "Search Patient",
            JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(input.trim());

            for (Patient p : patients) {
                if (p.id == id) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Patient ID: " + p.id +
                        "\nName: " + p.name +
                        "\nDepartment: " + p.department +
                        "\nPriority: " + p.priority +
                        "\nAssigned Bed: " + p.bed,
                        "Patient Details",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Patient not found.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid numeric patient ID.");
        }
    }

    private void scheduleAppointment() {
        if (patients.isEmpty()) {
            JOptionPane.showMessageDialog(
                this, "Register a patient before scheduling.");
            return;
        }

        JComboBox<Patient> patientBox =
            new JComboBox<>(patients.toArray(new Patient[0]));

        patientBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean selected, boolean focused) {

                super.getListCellRendererComponent(
                    list, value, index, selected, focused);

                if (value instanceof Patient) {
                    Patient p = (Patient) value;
                    setText(p.id + " - " + p.name);
                }
                return this;
            }
        });

        JTextField dateField = new JTextField("2026-10-26");
        JTextField timeField = new JTextField("10:30");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 10));
        form.add(new JLabel("Patient:"));
        form.add(patientBox);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(new JLabel("Time (HH:MM):"));
        form.add(timeField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Schedule Appointment",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        Patient p = (Patient) patientBox.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();

        if (p == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")
                || !time.matches("([01]\\d|2[0-3]):[0-5]\\d")) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid date and time.");
            return;
        }

        for (Appointment a : appointments) {
            if (a.department.equals(p.department)
                    && a.date.equals(date) && a.time.equals(time)) {
                JOptionPane.showMessageDialog(
                    this,
                    "This department already has an appointment " +
                    "at that time. Choose another time slot.");
                return;
            }
        }

        appointments.add(new Appointment(
            p.id, p.name, p.department, date, time));

        refreshTables();

        JOptionPane.showMessageDialog(
            this, "Appointment scheduled successfully!");
    }

    private void refreshTables() {
        patientModel.setRowCount(0);

        for (Patient p : patients) {
            patientModel.addRow(new Object[]{
                p.id, p.name, p.department, p.priority, p.bed
            });
        }

        appointmentModel.setRowCount(0);

        for (Appointment a : appointments) {
            appointmentModel.addRow(new Object[]{
                a.patientId, a.patientName,
                a.department, a.date, a.time
            });
        }

        int allocated = 0;

        for (Patient p : patients) {
            if (!p.bed.equals("Not Allocated")) {
                allocated++;
            }
        }

        totalLabel.setText(String.valueOf(patients.size()));
        allocatedLabel.setText(String.valueOf(allocated));
        availableLabel.setText(String.valueOf(beds.length - allocated));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Default Swing appearance will be used.
            }

            new HospitalSchedulingSystem().setVisible(true);
        });
    }
}
