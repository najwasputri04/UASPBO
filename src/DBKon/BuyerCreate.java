/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DBKon;

import java.sql.Statement;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class BuyerCreate extends javax.swing.JFrame {
    
    Koneksi kon;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BuyerCreate.class.getName());

    /**
     * Creates new form BuyerCreate
     */
    public BuyerCreate() {
        initComponents();
        kon = new Koneksi();
        this.setLocationRelativeTo(null);

        loadEventCombo();

        // Ticket category berubah otomatis saat event dipilih
        jEventCB.addActionListener(e -> loadTicketCombo());

        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new BuyerManagement().setVisible(true);
                dispose();
            }
        });
    }
    
    private void loadEventCombo() {
        jEventCB.removeAllItems();
        String sql = "SELECT event_id, event_name FROM event ORDER BY event_name";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Simpan format "id|nama" supaya bisa ambil event_id nanti
                jEventCB.addItem(rs.getInt("event_id") + "|" + rs.getString("event_name"));
            }
            // Setelah event terisi, langsung load ticket sesuai event pertama
            loadTicketCombo();
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load event: " + e.getMessage());
        }
    }

    private void loadTicketCombo() {
        jTicketCB.removeAllItems();
        if (jEventCB.getSelectedItem() == null) return;

        // Ambil event_id dari item yang dipilih (format "id|nama")
        String selected = jEventCB.getSelectedItem().toString();
        int eventId = Integer.parseInt(selected.split("\\|")[0]);

        String sql = "SELECT DISTINCT category FROM ticket WHERE event_id = ? ORDER BY category";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                jTicketCB.addItem(rs.getString("category"));
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load kategori tiket: " + e.getMessage());
        }
    }

    private void saveBuyer() {
        // Validasi field tidak boleh kosong
        String fullName = jNameField.getText().trim();
        String email    = jEmailField.getText().trim();
        String phone    = jNumberField.getText().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Semua field harus diisi!",
                "Peringatan",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (jEventCB.getSelectedItem() == null || jTicketCB.getSelectedItem() == null) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Pilih event dan kategori tiket!",
                "Peringatan",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil event_id dari combo
        int eventId = Integer.parseInt(jEventCB.getSelectedItem().toString().split("\\|")[0]);
        String category = jTicketCB.getSelectedItem().toString();

        try {
            kon.con.setAutoCommit(false);

            // 1. Insert ke tabel buyer
            String sqlBuyer = "INSERT INTO buyer (full_name, email_address, contact_number) VALUES (?, ?, ?)";
            int newBuyerId;
            try (java.sql.PreparedStatement ps = kon.con.prepareStatement(
                    sqlBuyer, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.executeUpdate();

                java.sql.ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    newBuyerId = generatedKeys.getInt(1);
                } else {
                    throw new java.sql.SQLException("Gagal mendapatkan buyer_id baru.");
                }
            }

            // 2. Cari ticket_id berdasarkan event + category
            String sqlTicket = "SELECT ticket_id FROM ticket WHERE event_id = ? AND category = ? LIMIT 1";
            int ticketId;
            try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlTicket)) {
                ps.setInt(1, eventId);
                ps.setString(2, category);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ticketId = rs.getInt("ticket_id");
                } else {
                    throw new java.sql.SQLException("Tiket tidak ditemukan untuk event dan kategori ini.");
                }
            }

            // 3. Insert ke tabel transaction
            String sqlTrx = "INSERT INTO `transaction` (buyer_id, ticket_id, transaction_date, quantity, total_price, payment_status) " +
                            "VALUES (?, ?, NOW(), 1, " +
                            "(SELECT price FROM ticket WHERE ticket_id = ?), 'pending')";
            try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlTrx)) {
                ps.setInt(1, newBuyerId);
                ps.setInt(2, ticketId);
                ps.setInt(3, ticketId);
                ps.executeUpdate();
            }

            kon.con.commit();
            javax.swing.JOptionPane.showMessageDialog(this, "Buyer berhasil ditambahkan!");

            // Kembali ke halaman management
            new BuyerManagement().setVisible(true);
            dispose();

        } catch (java.sql.SQLException e) {
            try { kon.con.rollback(); } catch (java.sql.SQLException ex) { /* abaikan */ }
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal simpan data: " + e.getMessage());
        } finally {
            try { kon.con.setAutoCommit(true); } catch (java.sql.SQLException ex) { /* abaikan */ }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jNameField = new javax.swing.JTextField();
        jEmailField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jNumberField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jEventCB = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jTicketCB = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Create Buyer Data");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(27, 46, 76));
        jLabel11.setText("CREATE BUYER DATA");

        jPanel1.setBackground(new java.awt.Color(27, 46, 76));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/chevron_left.png"))); // NOI18N
        jLabel17.setText("BACK");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel17)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addContainerGap())
        );

        jLabel19.setText("Full Name");

        jNameField.addActionListener(this::jNameFieldActionPerformed);

        jEmailField.addActionListener(this::jEmailFieldActionPerformed);

        jLabel20.setText("Email");

        jNumberField.addActionListener(this::jNumberFieldActionPerformed);

        jLabel21.setText("Contact Number");

        jLabel22.setText("Event Registered");

        jEventCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jEventCB.addActionListener(this::jEventCBActionPerformed);

        jLabel23.setText("Ticket Category");

        jTicketCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(27, 46, 76));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Submit");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jTicketCB, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jEventCB, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(374, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEventCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTicketCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEmailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jNumberField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(571, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(271, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNameFieldActionPerformed

    private void jEmailFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEmailFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEmailFieldActionPerformed

    private void jNumberFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNumberFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jNumberFieldActionPerformed

    private void jEventCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEventCBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEventCBActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        saveBuyer();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new BuyerCreate().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jEmailField;
    private javax.swing.JComboBox<String> jEventCB;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JTextField jNameField;
    private javax.swing.JTextField jNumberField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JComboBox<String> jTicketCB;
    // End of variables declaration//GEN-END:variables
}
