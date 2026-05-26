package DBKon;

public class updateEvent extends javax.swing.JFrame {

    private int eventId;

    public updateEvent(int id, String name, String location, String date, String desc, String status) {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Edit Event");
        this.eventId = id;
        txtName.setText(name);
        txtLocation.setText(location);
        txtDate.setText(date);
        txtDesc.setText(desc);
        cmbStatus.setSelectedItem(status);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        mainPanel   = new javax.swing.JPanel();
        titleLabel  = new javax.swing.JLabel();
        lblName     = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        lblDate     = new javax.swing.JLabel();
        lblDesc     = new javax.swing.JLabel();
        lblStatus   = new javax.swing.JLabel();
        txtName     = new javax.swing.JTextField();
        txtLocation = new javax.swing.JTextField();
        txtDate     = new javax.swing.JTextField();
        txtDesc     = new javax.swing.JTextField();
        cmbStatus   = new javax.swing.JComboBox<>();
        btnSimpan   = new javax.swing.JButton();
        btnBatal    = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 450));
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(8, 5, 8, 5);

        // ── Title ──
        titleLabel.setText("EDIT EVENT");
        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 20));
        titleLabel.setForeground(new java.awt.Color(27, 46, 76));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(0, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new java.awt.Insets(8, 5, 8, 5);

        // ── Fields (sama persis seperti addEvent) ──
        lblName.setText("Event Name");
        lblName.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblName.setForeground(new java.awt.Color(27, 46, 76));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        mainPanel.add(lblName, gbc);
        txtName.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtName.setPreferredSize(new java.awt.Dimension(250, 35));
        txtName.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        mainPanel.add(txtName, gbc);

        lblLocation.setText("Location");
        lblLocation.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblLocation.setForeground(new java.awt.Color(27, 46, 76));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        mainPanel.add(lblLocation, gbc);
        txtLocation.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtLocation.setPreferredSize(new java.awt.Dimension(250, 35));
        txtLocation.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        mainPanel.add(txtLocation, gbc);

        lblDate.setText("Date (YYYY-MM-DD)");
        lblDate.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblDate.setForeground(new java.awt.Color(27, 46, 76));
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        mainPanel.add(lblDate, gbc);
        txtDate.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtDate.setPreferredSize(new java.awt.Dimension(250, 35));
        txtDate.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        mainPanel.add(txtDate, gbc);

        lblDesc.setText("Description");
        lblDesc.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblDesc.setForeground(new java.awt.Color(27, 46, 76));
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        mainPanel.add(lblDesc, gbc);
        txtDesc.setFont(new java.awt.Font("Segoe UI", 0, 13));
        txtDesc.setPreferredSize(new java.awt.Dimension(250, 35));
        txtDesc.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        mainPanel.add(txtDesc, gbc);

        lblStatus.setText("Status");
        lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 13));
        lblStatus.setForeground(new java.awt.Color(27, 46, 76));
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        mainPanel.add(lblStatus, gbc);
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"Upcoming", "Confirmed", "Ongoing", "Completed", "Cancelled"}));
        cmbStatus.setFont(new java.awt.Font("Segoe UI", 0, 13));
        cmbStatus.setPreferredSize(new java.awt.Dimension(250, 35));
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        mainPanel.add(cmbStatus, gbc);

        // ── Buttons ──
        javax.swing.JPanel btnPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(java.awt.Color.WHITE);

        btnSimpan.setText("UPDATE");
        btnSimpan.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnSimpan.setBackground(new java.awt.Color(27, 46, 76));
        btnSimpan.setForeground(java.awt.Color.WHITE);
        btnSimpan.setPreferredSize(new java.awt.Dimension(120, 38));
        btnSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSimpan.setBorderPainted(false);
        btnSimpan.setFocusPainted(false);
        btnSimpan.addActionListener(evt -> btnSimpanActionPerformed(evt));

        btnBatal.setText("BATAL");
        btnBatal.setFont(new java.awt.Font("Segoe UI", 1, 13));
        btnBatal.setBackground(new java.awt.Color(255, 51, 51));
        btnBatal.setForeground(java.awt.Color.WHITE);
        btnBatal.setPreferredSize(new java.awt.Dimension(120, 38));
        btnBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBatal.setBorderPainted(false);
        btnBatal.setFocusPainted(false);
        btnBatal.addActionListener(evt -> btnBatalActionPerformed(evt));

        btnPanel.add(btnSimpan);
        btnPanel.add(btnBatal);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(20, 5, 0, 5);
        mainPanel.add(btnPanel, gbc);

        getContentPane().add(mainPanel);
        pack();
    }

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        String name     = txtName.getText().trim();
        String location = txtLocation.getText().trim();
        String date     = txtDate.getText().trim();
        String desc     = txtDesc.getText().trim();
        String status   = cmbStatus.getSelectedItem().toString();

        if (name.isEmpty() || location.isEmpty() || date.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Nama, Lokasi, dan Tanggal wajib diisi!",
                "Peringatan", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE event SET event_name=?, location=?, event_date=?, description=?, status=? WHERE event_id=?";
        try (java.sql.Connection con = new Koneksi().con;
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setString(3, date);
            ps.setString(4, desc);
            ps.setString(5, status);
            ps.setInt(6, eventId);
            ps.executeUpdate();
            javax.swing.JOptionPane.showMessageDialog(this, "Event berhasil diupdate!");
            this.dispose();
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal: " + e.getMessage());
        }
    }

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // Variables
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel lblName, lblLocation, lblDate, lblDesc, lblStatus;
    private javax.swing.JTextField txtName, txtLocation, txtDate, txtDesc;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JButton btnSimpan, btnBatal;
}