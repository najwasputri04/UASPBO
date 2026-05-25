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
        jPanel10 = new javax.swing.JPanel();
        eventPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        vendorPanel1 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        gsPanel1 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        ticketPanel1 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        buyerPanel1 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        jPanel10.setBackground(new java.awt.Color(27, 42, 79));
        jPanel10.setPreferredSize(new java.awt.Dimension(239, 600));

        eventPanel1.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel18.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/calender_hitam.png"))); // NOI18N

        jLabel24.setBackground(new java.awt.Color(27, 42, 79));
        jLabel24.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(27, 42, 79));
        jLabel24.setText("[EVENT]");

        jLabel26.setBackground(new java.awt.Color(27, 42, 79));
        jLabel26.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(27, 42, 79));
        jLabel26.setText("Manage Event");

        javax.swing.GroupLayout eventPanel1Layout = new javax.swing.GroupLayout(eventPanel1);
        eventPanel1.setLayout(eventPanel1Layout);
        eventPanel1Layout.setHorizontalGroup(
            eventPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(eventPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jLabel24))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        eventPanel1Layout.setVerticalGroup(
            eventPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanel1Layout.createSequentialGroup()
                .addGroup(eventPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(eventPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel18))
                    .addGroup(eventPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel26)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        vendorPanel1.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/localShipping_hitam.png"))); // NOI18N

        jLabel28.setBackground(new java.awt.Color(255, 255, 255));
        jLabel28.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(27, 42, 79));
        jLabel28.setText("[VENDOR]");

        jLabel29.setBackground(new java.awt.Color(27, 42, 79));
        jLabel29.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(27, 42, 79));
        jLabel29.setText("Manage Vendor");

        javax.swing.GroupLayout vendorPanel1Layout = new javax.swing.GroupLayout(vendorPanel1);
        vendorPanel1.setLayout(vendorPanel1Layout);
        vendorPanel1Layout.setHorizontalGroup(
            vendorPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vendorPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vendorPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        vendorPanel1Layout.setVerticalGroup(
            vendorPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vendorPanel1Layout.createSequentialGroup()
                .addGroup(vendorPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vendorPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel27))
                    .addGroup(vendorPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel29)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        gsPanel1.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/mic_hitam.png"))); // NOI18N

        jLabel31.setBackground(new java.awt.Color(27, 42, 79));
        jLabel31.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(27, 42, 79));
        jLabel31.setText("[GUEST STAR]");

        jLabel32.setBackground(new java.awt.Color(27, 42, 79));
        jLabel32.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(27, 42, 79));
        jLabel32.setText("Manage Guest");

        javax.swing.GroupLayout gsPanel1Layout = new javax.swing.GroupLayout(gsPanel1);
        gsPanel1.setLayout(gsPanel1Layout);
        gsPanel1Layout.setHorizontalGroup(
            gsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gsPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel30)
                .addGap(18, 18, 18)
                .addGroup(gsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(jLabel31))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        gsPanel1Layout.setVerticalGroup(
            gsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gsPanel1Layout.createSequentialGroup()
                .addGroup(gsPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(gsPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel30))
                    .addGroup(gsPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel31)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        ticketPanel1.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/ticket_hitam.png"))); // NOI18N

        jLabel34.setBackground(new java.awt.Color(27, 42, 79));
        jLabel34.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(27, 42, 79));
        jLabel34.setText("[TICKETS]");

        jLabel35.setBackground(new java.awt.Color(27, 42, 79));
        jLabel35.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(27, 42, 79));
        jLabel35.setText("Manage Ticket");

        javax.swing.GroupLayout ticketPanel1Layout = new javax.swing.GroupLayout(ticketPanel1);
        ticketPanel1.setLayout(ticketPanel1Layout);
        ticketPanel1Layout.setHorizontalGroup(
            ticketPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addGroup(ticketPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35)
                    .addComponent(jLabel34))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        ticketPanel1Layout.setVerticalGroup(
            ticketPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanel1Layout.createSequentialGroup()
                .addGroup(ticketPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ticketPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel33))
                    .addGroup(ticketPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        buyerPanel1.setBackground(new java.awt.Color(255, 102, 102));
        buyerPanel1.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel36.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/user_putih.png"))); // NOI18N

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("[BUYER]");

        jLabel38.setBackground(new java.awt.Color(27, 42, 79));
        jLabel38.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("Manage Buyer");

        javax.swing.GroupLayout buyerPanel1Layout = new javax.swing.GroupLayout(buyerPanel1);
        buyerPanel1.setLayout(buyerPanel1Layout);
        buyerPanel1Layout.setHorizontalGroup(
            buyerPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyerPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel36)
                .addGap(18, 18, 18)
                .addGroup(buyerPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(jLabel37))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        buyerPanel1Layout.setVerticalGroup(
            buyerPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyerPanel1Layout.createSequentialGroup()
                .addGroup(buyerPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buyerPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel36))
                    .addGroup(buyerPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel38)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vendorPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ticketPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(eventPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(vendorPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(gsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(ticketPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(buyerPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(362, Short.MAX_VALUE))
        );

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
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
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
                .addContainerGap(122, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 903, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 277, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JPanel buyerPanel1;
    private javax.swing.JPanel eventPanel1;
    private javax.swing.JPanel gsPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JTextField jEmailField;
    private javax.swing.JComboBox<String> jEventCB;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JTextField jNameField;
    private javax.swing.JTextField jNumberField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JComboBox<String> jTicketCB;
    private javax.swing.JPanel ticketPanel1;
    private javax.swing.JPanel vendorPanel1;
    // End of variables declaration//GEN-END:variables
}
