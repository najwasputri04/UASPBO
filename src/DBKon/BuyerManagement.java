/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package DBKon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author user
 */
public class BuyerManagement extends javax.swing.JFrame {
    
    Koneksi kon;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(BuyerManagement.class.getName());
    
    private javax.swing.table.DefaultTableModel tableModel;

    /**
     * Creates new form BuyerManagement
     */
    public BuyerManagement() {
        initComponents();
        tableBuyer.setRowSelectionAllowed(true);
        tableBuyer.setSelectionMode(
            javax.swing.ListSelectionModel.SINGLE_SELECTION
        );
        kon = new Koneksi();
        initTableModel();
        try {
            loadBuyerData();
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        
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
        
        label21.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new BuyerCreate().setVisible(true);
                dispose();
            }
        });
        
        panelBtnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int selectedRow = -1;
                int totalChecked = 0;

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Boolean checked = (Boolean) tableModel.getValueAt(i, 0);

                    if (checked != null && checked) {
                        selectedRow = i;
                        totalChecked++;
                    }
                }

                if (totalChecked == 0) {
                    javax.swing.JOptionPane.showMessageDialog(
                            BuyerManagement.this,
                            "Pilih satu data buyer!",
                            "Peringatan",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (totalChecked > 1) {
                    javax.swing.JOptionPane.showMessageDialog(
                            BuyerManagement.this,
                            "Edit hanya bisa satu data!",
                            "Peringatan",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String id = tableModel.getValueAt(selectedRow, 1).toString();

                BuyerEdit edit = new BuyerEdit(id);
                edit.setVisible(true);

                dispose();
            }
        });
        
        panelBtnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                // Cek dulu apakah ada yang dicentang
                boolean adaYangDipilih = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Boolean checked = (Boolean) tableModel.getValueAt(i, 0);
                    if (checked != null && checked) {
                        adaYangDipilih = true;
                        break;
                    }
                }

                if (!adaYangDipilih) {
                    javax.swing.JOptionPane.showMessageDialog(
                            BuyerManagement.this,
                            "Pilih minimal satu data!",
                            "Peringatan",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // ✅ TAMBAHAN: konfirmasi sebelum hapus
                int confirm = javax.swing.JOptionPane.showConfirmDialog(
                        BuyerManagement.this,
                        "Yakin ingin menghapus data yang dipilih?\nSemua transaksi terkait juga akan ikut terhapus.",
                        "Konfirmasi Hapus",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.WARNING_MESSAGE);

                if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                    return; // Batal hapus
                }

                // Lanjut hapus jika user klik YES
                java.util.ArrayList<String> selectedIds = new java.util.ArrayList<>();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Boolean checked = (Boolean) tableModel.getValueAt(i, 0);

                    if (checked != null && checked) {
                        String id = tableModel.getValueAt(i, 1).toString();
                        selectedIds.add(id);
                    }
                }

                // Hapus semua data terpilih
                for (String id : selectedIds) {
                    deleteBuyer(id);
                }

                // Refresh tabel
                loadBuyerData();
                
                javax.swing.JOptionPane.showMessageDialog(
                    BuyerManagement.this,
                    "Data berhasil dihapus!"
                );
            }
        });
    }
    
    private void initTableModel(){
        String[] columns = {"SELECT","BUYER ID", "FULL NAME", "EMAIL", "CONTACT NUMBER", "EVENT REGISTERED", "TICKET CATEGORY"};
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0){
            @Override
            public Class<?> getColumnClass(int column){
                if(column == 0){
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int col){
                return col == 0;
            }
        };
        tableBuyer.setModel(tableModel);
        tableBuyer.getColumnModel().getColumn(0).setMaxWidth(60);
tableBuyer.getColumnModel().getColumn(0).setMinWidth(60);
tableBuyer.getColumnModel().getColumn(0).setPreferredWidth(60);
    }
    
    private void loadBuyerData(){
        tableModel.setRowCount(0);
        
        String sql = "SELECT b.buyer_id, b.full_name, b.email_address, b.contact_number, " +
                    "e.event_name, t.category " +
                    "FROM buyer b " +
                    "LEFT JOIN `transaction` tr ON tr.transaction_id = (" +
                    "    SELECT transaction_id FROM `transaction` " +
                    "    WHERE buyer_id = b.buyer_id " +
                    "    ORDER BY transaction_date DESC LIMIT 1" +
                    ") " +
                    "LEFT JOIN ticket t ON tr.ticket_id = t.ticket_id " +
                    "LEFT JOIN event e ON t.event_id = e.event_id " +
                    "ORDER BY b.buyer_id";
        
        try (   java.sql.PreparedStatement ps = kon.con.prepareStatement(sql);
                java.sql.ResultSet rs = ps.executeQuery()) { 
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    false,
                    rs.getString("buyer_id"),
                    rs.getString("full_name"),
                    rs.getString("email_address"),
                    rs.getString("contact_number"),
                    rs.getString("event_name"), 
                    rs.getString("category") 
                });
            }
            lblCountAttendees.setText("Registered Attendees: " + tableModel.getRowCount());
        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
        }
    }

    private void deleteBuyer(String buyerId) {
        // Hapus transaksi terkait dulu (FK constraint)
        String sqlDelTrx = "DELETE FROM `transaction` WHERE buyer_id = ?";
        String sqlDelBuyer = "DELETE FROM buyer WHERE buyer_id = ?";
 
        try {
            kon.con.setAutoCommit(false);
 
            try (java.sql.PreparedStatement ps1 = kon.con.prepareStatement(sqlDelTrx)) {
                ps1.setString(1, buyerId);
                ps1.executeUpdate();
            }
 
            try (java.sql.PreparedStatement ps2 = kon.con.prepareStatement(sqlDelBuyer)) {
                ps2.setString(1, buyerId);
                ps2.executeUpdate();
            }
 
            kon.con.commit();
 
        } catch (java.sql.SQLException e) {
            try { kon.con.rollback(); } catch (java.sql.SQLException ex) { /* abaikan */ }
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Gagal menghapus: " + e.getMessage());
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

        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        panelBtnAdd = new javax.swing.JPanel();
        label21 = new javax.swing.JLabel();
        panelBtnEdit = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        panelBtnDelete = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableBuyer = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        lblCountAttendees = new javax.swing.JLabel();
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

        jPanel1.setMinimumSize(new java.awt.Dimension(1000, 625));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(27, 46, 76));
        jLabel11.setText("BUYER DATA MANAGEMENT");

        panelBtnAdd.setBackground(new java.awt.Color(27, 46, 76));

        label21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        label21.setForeground(new java.awt.Color(255, 255, 255));
        label21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/add_putih.png"))); // NOI18N
        label21.setText("ADD NEW BUYER");

        javax.swing.GroupLayout panelBtnAddLayout = new javax.swing.GroupLayout(panelBtnAdd);
        panelBtnAdd.setLayout(panelBtnAddLayout);
        panelBtnAddLayout.setHorizontalGroup(
            panelBtnAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBtnAddLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(label21, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBtnAddLayout.setVerticalGroup(
            panelBtnAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBtnAddLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelBtnEdit.setBackground(new java.awt.Color(27, 46, 76));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/edit_putih.png"))); // NOI18N
        jLabel21.setText("EDIT SELECTED");

        javax.swing.GroupLayout panelBtnEditLayout = new javax.swing.GroupLayout(panelBtnEdit);
        panelBtnEdit.setLayout(panelBtnEditLayout);
        panelBtnEditLayout.setHorizontalGroup(
            panelBtnEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBtnEditLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBtnEditLayout.setVerticalGroup(
            panelBtnEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBtnEditLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addContainerGap())
        );

        panelBtnDelete.setBackground(new java.awt.Color(255, 0, 0));

        jPanel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jPanel20.setForeground(new java.awt.Color(255, 255, 255));
        jPanel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/delete_putih.png"))); // NOI18N
        jPanel20.setText("DELETE SELECTED");

        javax.swing.GroupLayout panelBtnDeleteLayout = new javax.swing.GroupLayout(panelBtnDelete);
        panelBtnDelete.setLayout(panelBtnDeleteLayout);
        panelBtnDeleteLayout.setHorizontalGroup(
            panelBtnDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBtnDeleteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelBtnDeleteLayout.setVerticalGroup(
            panelBtnDeleteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBtnDeleteLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel20)
                .addContainerGap())
        );

        tableBuyer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "BUYER ID", "FULL NAME", "EMAIL", "CONTACT NUMBER", "EVENT REGISTERED", "TICKET CATEGORY"
            }
        ));
        jScrollPane1.setViewportView(tableBuyer);

        txtSearch.addActionListener(this::txtSearchActionPerformed);

        btnSearch.setText("CARI");
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(panelBtnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelBtnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelBtnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 422, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 914, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelBtnDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBtnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBtnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/DBKon/img/info.png"))); // NOI18N
        jLabel20.setText("Quick Info");

        lblCountAttendees.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblCountAttendees.setText("Registered Attendees:");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCountAttendees)
                    .addComponent(jLabel20))
                .addContainerGap(642, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCountAttendees)
                .addContainerGap(18, Short.MAX_VALUE))
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
                .addGap(25, 25, 25)
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
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(99, 99, 99))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 108, Short.MAX_VALUE))
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        String keyword = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        String sql = "SELECT b.buyer_id, b.full_name, b.email_address, b.contact_number, " +
                     "e.event_name, t.category " +
                     "FROM buyer b " +
                     "LEFT JOIN transaction tr ON b.buyer_id = tr.buyer_id " +
                     "LEFT JOIN ticket t ON tr.ticket_id = t.ticket_id " +
                     "LEFT JOIN event e ON t.event_id = e.event_id " +
                     "WHERE LOWER(b.full_name) LIKE ? OR LOWER(b.buyer_id) LIKE ?";

        try (java.sql.PreparedStatement ps = kon.con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("buyer_id"),
                    rs.getString("full_name"),
                    rs.getString("email_address"),
                    rs.getString("contact_number"),
                    rs.getString("event_name"),
                    rs.getString("category")
                });
            }

            lblCountAttendees.setText("Registered Attendees: " + tableModel.getRowCount());

        } catch (java.sql.SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Gagal mencari: " + e.getMessage());
        }
    }//GEN-LAST:event_btnSearchActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new BuyerManagement().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearch;
    private javax.swing.JPanel buyerPanel;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JLabel jPanel20;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label21;
    private javax.swing.JLabel lblCountAttendees;
    private javax.swing.JPanel panelBtnAdd;
    private javax.swing.JPanel panelBtnDelete;
    private javax.swing.JPanel panelBtnEdit;
    private javax.swing.JTable tableBuyer;
    private javax.swing.JPanel ticketPanel;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JPanel vendorPanel;
    // End of variables declaration//GEN-END:variables
}
