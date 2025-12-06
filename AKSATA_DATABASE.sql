


create database db_aksata;

use db_aksata;


CREATE TABLE cabang (
    id_cabang INT AUTO_INCREMENT PRIMARY KEY,
    nama_cabang VARCHAR(100) NOT NULL,
    alamat TEXT,
    kepala_cabang VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'barista', 'manager') NOT NULL,
    nama_lengkap VARCHAR(100),
    id_cabang INT, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang) ON DELETE SET NULL
);


CREATE TABLE master_bahan (
    id_bahan INT AUTO_INCREMENT PRIMARY KEY,
    nama_bahan VARCHAR(100) NOT NULL, 
    satuan VARCHAR(20) NOT NULL,      
    harga_per_unit DOUBLE DEFAULT 0   
);


CREATE TABLE stok_cabang (
    id_stok INT AUTO_INCREMENT PRIMARY KEY,
    id_cabang INT NOT NULL,
    id_bahan INT NOT NULL,
    stok_sistem DOUBLE DEFAULT 0, 
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang) ON DELETE CASCADE,
    FOREIGN KEY (id_bahan) REFERENCES master_bahan(id_bahan) ON DELETE CASCADE,
    UNIQUE KEY unique_stok (id_cabang, id_bahan) 
);


CREATE TABLE menu (
    id_menu INT AUTO_INCREMENT PRIMARY KEY,
    nama_menu VARCHAR(100) NOT NULL,
    kategori ENUM('coffee', 'non-coffee', 'snack', 'beans'),
    deskripsi TEXT,
    nama_petani VARCHAR(100),
    teknik_brew VARCHAR(50),
    level_acidity INT DEFAULT 0,    
    level_sweetness INT DEFAULT 0,  
    level_bitterness INT DEFAULT 0, 
    level_body INT DEFAULT 0,       
    
    gambar_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE menu_harga (
    id_harga INT AUTO_INCREMENT PRIMARY KEY,
    id_menu INT NOT NULL,
    ukuran ENUM('regular', 'large', '1liter') NOT NULL,
    harga_jual DOUBLE NOT NULL,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu) ON DELETE CASCADE
);


CREATE TABLE menu_cabang (
    id_mc INT AUTO_INCREMENT PRIMARY KEY,
    id_menu INT NOT NULL,
    id_cabang INT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu) ON DELETE CASCADE,
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang) ON DELETE CASCADE
);


CREATE TABLE resep (
    id_resep INT AUTO_INCREMENT PRIMARY KEY,
    id_menu INT NOT NULL,
    ukuran ENUM('regular', 'large', '1liter') NOT NULL,
    id_bahan INT NOT NULL, 
    jumlah_pakai DOUBLE NOT NULL, 
    
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu) ON DELETE CASCADE,
    FOREIGN KEY (id_bahan) REFERENCES master_bahan(id_bahan) ON DELETE RESTRICT
);


CREATE TABLE addons (
    id_addon INT AUTO_INCREMENT PRIMARY KEY,
    nama_addon VARCHAR(50), 
    harga DOUBLE,
    id_bahan_terkait INT,   
    jumlah_pakai_bahan DOUBLE,
    FOREIGN KEY (id_bahan_terkait) REFERENCES master_bahan(id_bahan)
);


CREATE TABLE diskon (
    id_diskon INT AUTO_INCREMENT PRIMARY KEY,
    nama_promo VARCHAR(100),
    tipe ENUM('persen', 'nominal') NOT NULL,
    nilai DOUBLE NOT NULL,
    min_belanja DOUBLE DEFAULT 0,
    start_date DATE,
    end_date DATE
);


CREATE TABLE diskon_cabang (
    id_dc INT AUTO_INCREMENT PRIMARY KEY,
    id_diskon INT NOT NULL,
    id_cabang INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_diskon) REFERENCES diskon(id_diskon) ON DELETE CASCADE,
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang) ON DELETE CASCADE
);


CREATE TABLE transaksi (
    id_transaksi VARCHAR(20) PRIMARY KEY, 
    tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    id_cabang INT NOT NULL,
    id_user INT,            
    nama_customer VARCHAR(50),
    
    tipe_pesanan ENUM('dine_in', 'take_away', 'ojol') NOT NULL,
    status_pesanan ENUM('pending', 'diproses', 'siap', 'selesai', 'batal') DEFAULT 'pending',
    
    
    subtotal DOUBLE, 
    id_diskon INT,   
    total_potongan DOUBLE DEFAULT 0,
    total_bayar DOUBLE, 
    metode_bayar VARCHAR(50), 
    
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang),
    FOREIGN KEY (id_user) REFERENCES users(id_user),
    FOREIGN KEY (id_diskon) REFERENCES diskon(id_diskon)
);


CREATE TABLE detail_transaksi (
    id_detail INT AUTO_INCREMENT PRIMARY KEY,
    id_transaksi VARCHAR(20) NOT NULL,
    id_menu INT NOT NULL,
    
    
    ukuran ENUM('regular', 'large', '1liter'),
    qty INT NOT NULL,
    sugar_level ENUM('normal', 'less', 'no', 'separate'),
    ice_level ENUM('normal', 'less', 'no'),
    
    harga_satuan DOUBLE, 
    subtotal_item DOUBLE,
    catatan TEXT,
    
    FOREIGN KEY (id_transaksi) REFERENCES transaksi(id_transaksi) ON DELETE CASCADE,
    FOREIGN KEY (id_menu) REFERENCES menu(id_menu)
);


CREATE TABLE detail_transaksi_addons (
    id_dta INT AUTO_INCREMENT PRIMARY KEY,
    id_detail INT NOT NULL, 
    id_addon INT NOT NULL,
    FOREIGN KEY (id_detail) REFERENCES detail_transaksi(id_detail) ON DELETE CASCADE,
    FOREIGN KEY (id_addon) REFERENCES addons(id_addon)
);


CREATE TABLE log_stok_opname (
    id_log INT AUTO_INCREMENT PRIMARY KEY,
    tanggal DATE NOT NULL,
    id_cabang INT NOT NULL,
    id_bahan INT NOT NULL,
    
    stok_sistem DOUBLE,    
    stok_fisik DOUBLE,     
    selisih DOUBLE,        
    nilai_kerugian DOUBLE, 
    
    keterangan TEXT,       
    id_user INT,           
    
    FOREIGN KEY (id_cabang) REFERENCES cabang(id_cabang),
    FOREIGN KEY (id_bahan) REFERENCES master_bahan(id_bahan),
    FOREIGN KEY (id_user) REFERENCES users(id_user)
);


USE db_aksata;


INSERT INTO cabang (id_cabang, nama_cabang, alamat) VALUES 
(1, 'Aksata Pusat', 'Jl. Dago No 1');


INSERT INTO master_bahan (id_bahan, nama_bahan, satuan, harga_per_unit) VALUES 
(1, 'Arabica Beans', 'gram', 250),
(2, 'Fresh Milk', 'ml', 18);


INSERT INTO stok_cabang (id_cabang, id_bahan, stok_sistem) VALUES 
(1, 1, 1000), 
(1, 2, 5000); 


INSERT INTO menu (id_menu, nama_menu, kategori, deskripsi, nama_petani) VALUES 
(1, 'Kopi Susu Aksata', 'coffee', 'Kopi susu creamy gula aren', 'Pak Asep Ciwidey');



INSERT INTO resep (id_menu, ukuran, id_bahan, jumlah_pakai) VALUES 
(1, 'regular', 1, 18),
(1, 'regular', 2, 150);