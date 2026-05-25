/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DBKon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author user
 */
public class BuyerEdit extends javax.swing.JFrame {
    
    Koneksi kon;
    private String buyerId;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BuyerEdit.class.getName());

    /**
     * Creates new form BuyerEdit
     */
    public BuyerEdit(String buyerId) {
        initComponents();

        kon = new Koneksi();
        this.buyerId = buyerId;
        
        vendorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new vendor().setVisible(true);
                dispose();
            }
        });
        gsPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new GuestStarManagement().setVisible(true);
                dispose();
            }
        });
        ticketPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new tiket().setVisible(true);
                dispose();
            }
        });

        txtBuyerId.setEditable(false);

        loadEventCombo();
        loadBuyerData();

        jLabel25.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                saveChanges();
            }
        });

        lblBtnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new BuyerManagement().setVisible(true);
                dispose();
            }
        });
    }
    
    private void loadEventCombo(){
        cmbEvent.removeAllItems();
        String query_edit = "SELECT event_id, event_name FROM event";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(query_edit); 
                java.sql.ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                cmbEvent.addItem(rs.getString("event_name"));
            }
        } catch (java.sql.SQLException e){
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load event: " + e.getMessage());
        }
        cmbTicketCategory.removeAllItems();
        String sql2 = "SELECT DISTINCT category FROM ticket";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sql2);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbTicketCategory.addItem(rs.getString("category"));
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load kategori: " + e.getMessage());
        }
    }
    
    private void loadBuyerData() {
        String sqlBuyer = "SELECT buyer_id, full_name, email_address, contact_number FROM buyer WHERE buyer_id = ?";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlBuyer)) {
            ps.setString(1, buyerId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtBuyerId.setText(rs.getString("buyer_id"));
                txtFullName.setText(rs.getString("full_name"));
                txtEmail.setText(rs.getString("email_address"));
                txtContactNumber.setText(rs.getString("contact_number"));
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load data buyer: " + e.getMessage());
        }

        String sqlTrx = "SELECT e.event_name, t.category " +
                        "FROM `transaction` tr " +
                        "JOIN ticket t ON tr.ticket_id = t.ticket_id " +
                        "JOIN event e ON t.event_id = e.event_id " +
                        "WHERE tr.buyer_id = ? " +
                        "ORDER BY tr.transaction_date DESC LIMIT 1";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlTrx)) {
            ps.setString(1, buyerId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String eventName = rs.getString("event_name");
                String category  = rs.getString("category");

                // Set combobox dengan loop untuk memastikan item ketemu
                for (int i = 0; i < cmbEvent.getItemCount(); i++) {
                    if (cmbEvent.getItemAt(i).equals(eventName)) {
                        cmbEvent.setSelectedIndex(i);
                        break;
                    }
                }
                for (int i = 0; i < cmbTicketCategory.getItemCount(); i++) {
                    if (cmbTicketCategory.getItemAt(i).equals(category)) {
                        cmbTicketCategory.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal load transaksi: " + e.getMessage());
        }
    }
    
    private void saveChanges() {
        // 1. Update data buyer
        String sqlBuyer = "UPDATE buyer SET full_name=?, email_address=?, contact_number=? WHERE buyer_id=?";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlBuyer)) {
            ps.setString(1, txtFullName.getText().trim());
            ps.setString(2, txtEmail.getText().trim());
            ps.setString(3, txtContactNumber.getText().trim());
            ps.setString(4, buyerId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal update buyer: " + e.getMessage());
            return;
        }

        // 2. Cari ticket_id berdasarkan event + category yang dipilih
        String selectedEvent    = cmbEvent.getSelectedItem().toString();
        String selectedCategory = cmbTicketCategory.getSelectedItem().toString();

        String sqlTicket = "SELECT t.ticket_id FROM ticket t " +
                           "JOIN event e ON t.event_id = e.event_id " +
                           "WHERE e.event_name = ? AND t.category = ? LIMIT 1";

        int ticketId = -1;
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlTicket)) {
            ps.setString(1, selectedEvent);
            ps.setString(2, selectedCategory);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticketId = rs.getInt("ticket_id");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Kombinasi event dan kategori tiket tidak ditemukan!");
                return;
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal cari ticket: " + e.getMessage());
            return;
        }

        // 3. Update transaksi terakhir buyer dengan ticket_id baru
        String sqlTrx = "UPDATE `transaction` SET ticket_id = ? " +
                        "WHERE buyer_id = ? " +
                        "ORDER BY transaction_date DESC LIMIT 1";
        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sqlTrx)) {
            ps.setInt(1, ticketId);
            ps.setString(2, buyerId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Buyer tidak punya transaksi, hanya data profil yang diupdate.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            }
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal update transaksi: " + e.getMessage());
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
        lblBtnBack = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtBuyerId = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtFullName = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtContactNumber = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cmbEvent = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        cmbTicketCategory = new javax.swing.JComboBox<>();
        lblBtnSave = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        eventPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        vendorPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        gsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ticketPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        buyerPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(27, 46, 76));
        jLabel11.setText("EDIT BUYER DATA");

        jPanel1.setBackground(new java.awt.Color(27, 46, 76));

        lblBtnBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBtnBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBtnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/chevron_left.png"))); // NOI18N
        lblBtnBack.setText("BACK");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblBtnBack)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblBtnBack)
                .addContainerGap())
        );

        jLabel18.setText("Buyer ID");

        txtBuyerId.addActionListener(this::txtBuyerIdActionPerformed);

        jLabel19.setText("Full Name");

        txtFullName.addActionListener(this::txtFullNameActionPerformed);

        txtEmail.addActionListener(this::txtEmailActionPerformed);

        jLabel20.setText("Email");

        txtContactNumber.addActionListener(this::txtContactNumberActionPerformed);

        jLabel21.setText("Contact Number");

        jLabel22.setText("Event Registered");

        cmbEvent.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel23.setText("Ticket Category");

        cmbTicketCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblBtnSave.setBackground(new java.awt.Color(27, 46, 76));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/save.png"))); // NOI18N
        jLabel25.setText("SAVE CHANGES");

        javax.swing.GroupLayout lblBtnSaveLayout = new javax.swing.GroupLayout(lblBtnSave);
        lblBtnSave.setLayout(lblBtnSaveLayout);
        lblBtnSaveLayout.setHorizontalGroup(
            lblBtnSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lblBtnSaveLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel25)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        lblBtnSaveLayout.setVerticalGroup(
            lblBtnSaveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lblBtnSaveLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(txtBuyerId, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addGap(57, 57, 57)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(cmbTicketCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(cmbEvent, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblBtnSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(371, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(46, 46, 46)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBuyerId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbTicketCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(lblBtnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(293, 293, 293))
        );

        jPanel10.setBackground(new java.awt.Color(27, 42, 79));
        jPanel10.setPreferredSize(new java.awt.Dimension(239, 600));

        eventPanel.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/calender_hitam.png"))); // NOI18N

        jLabel2.setBackground(new java.awt.Color(27, 42, 79));
        jLabel2.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(27, 42, 79));
        jLabel2.setText("[EVENT]");

        jLabel3.setBackground(new java.awt.Color(27, 42, 79));
        jLabel3.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(27, 42, 79));
        jLabel3.setText("Manage Event");

        javax.swing.GroupLayout eventPanelLayout = new javax.swing.GroupLayout(eventPanel);
        eventPanel.setLayout(eventPanelLayout);
        eventPanelLayout.setHorizontalGroup(
            eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        eventPanelLayout.setVerticalGroup(
            eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(eventPanelLayout.createSequentialGroup()
                .addGroup(eventPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(eventPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel1))
                    .addGroup(eventPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        vendorPanel.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/localShipping_hitam.png"))); // NOI18N

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(27, 42, 79));
        jLabel4.setText("[VENDOR]");

        jLabel5.setBackground(new java.awt.Color(27, 42, 79));
        jLabel5.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(27, 42, 79));
        jLabel5.setText("Manage Vendor");

        javax.swing.GroupLayout vendorPanelLayout = new javax.swing.GroupLayout(vendorPanel);
        vendorPanel.setLayout(vendorPanelLayout);
        vendorPanelLayout.setHorizontalGroup(
            vendorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vendorPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(vendorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        vendorPanelLayout.setVerticalGroup(
            vendorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(vendorPanelLayout.createSequentialGroup()
                .addGroup(vendorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(vendorPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel6))
                    .addGroup(vendorPanelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        gsPanel.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/mic_hitam.png"))); // NOI18N

        jLabel8.setBackground(new java.awt.Color(27, 42, 79));
        jLabel8.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(27, 42, 79));
        jLabel8.setText("[GUEST STAR]");

        jLabel9.setBackground(new java.awt.Color(27, 42, 79));
        jLabel9.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(27, 42, 79));
        jLabel9.setText("Manage Guest");

        javax.swing.GroupLayout gsPanelLayout = new javax.swing.GroupLayout(gsPanel);
        gsPanel.setLayout(gsPanelLayout);
        gsPanelLayout.setHorizontalGroup(
            gsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gsPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(gsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        gsPanelLayout.setVerticalGroup(
            gsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gsPanelLayout.createSequentialGroup()
                .addGroup(gsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(gsPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel7))
                    .addGroup(gsPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        ticketPanel.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/ticket_hitam.png"))); // NOI18N

        jLabel12.setBackground(new java.awt.Color(27, 42, 79));
        jLabel12.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(27, 42, 79));
        jLabel12.setText("[TICKETS]");

        jLabel13.setBackground(new java.awt.Color(27, 42, 79));
        jLabel13.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(27, 42, 79));
        jLabel13.setText("Manage Ticket");

        javax.swing.GroupLayout ticketPanelLayout = new javax.swing.GroupLayout(ticketPanel);
        ticketPanel.setLayout(ticketPanelLayout);
        ticketPanelLayout.setHorizontalGroup(
            ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel12))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        ticketPanelLayout.setVerticalGroup(
            ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ticketPanelLayout.createSequentialGroup()
                .addGroup(ticketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ticketPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel10))
                    .addGroup(ticketPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        buyerPanel.setBackground(new java.awt.Color(255, 102, 102));
        buyerPanel.setPreferredSize(new java.awt.Dimension(196, 81));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/user_putih.png"))); // NOI18N

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Poppins", 1, 15)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("[BUYER]");

        jLabel16.setBackground(new java.awt.Color(27, 42, 79));
        jLabel16.setFont(new java.awt.Font("Poppins Medium", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Manage Buyer");

        javax.swing.GroupLayout buyerPanelLayout = new javax.swing.GroupLayout(buyerPanel);
        buyerPanel.setLayout(buyerPanelLayout);
        buyerPanelLayout.setHorizontalGroup(
            buyerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyerPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(buyerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(jLabel15))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        buyerPanelLayout.setVerticalGroup(
            buyerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buyerPanelLayout.createSequentialGroup()
                .addGroup(buyerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buyerPanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel14))
                    .addGroup(buyerPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vendorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eventPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ticketPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(eventPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(vendorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(gsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(ticketPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(buyerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 5, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuyerIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuyerIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuyerIdActionPerformed

    private void txtFullNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFullNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFullNameActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtContactNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContactNumberActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> 
            new BuyerEdit("B001").setVisible(true)
        );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buyerPanel;
    private javax.swing.JComboBox<String> cmbEvent;
    private javax.swing.JComboBox<String> cmbTicketCategory;
    private javax.swing.JPanel eventPanel;
    private javax.swing.JPanel gsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel lblBtnBack;
    private javax.swing.JPanel lblBtnSave;
    private javax.swing.JPanel ticketPanel;
    private javax.swing.JTextField txtBuyerId;
    private javax.swing.JTextField txtContactNumber;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFullName;
    private javax.swing.JPanel vendorPanel;
    // End of variables declaration//GEN-END:variables
}
