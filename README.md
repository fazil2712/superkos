```markdown
# ⚡ 🏠 SUPERKOS 🏠 ⚡

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=28&duration=2000&pause=500&color=FF5733&center=true&vCenter=true&width=500&lines=%F0%9F%9A%80+Welcome+to+Superkos%EF%B8%8F!;%E2%9C%A8+Aplikasi+Anak+Kos+Paling+Skena%E2%9C%A8" alt="Typing SVG" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-green?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apache-maven&logoColor=white" />
</p>

---

## 🔥 Overview
Halo **Gen-Z Developers**! 😎 Siap buat *deployment* local paling lancar jaya sedunia? Ini adalah panduan *step-by-step* buat nge-run project **Superkos** di laptop kalian tanpa drama, tanpa *overthinking*. Let's goooo! ✨

---

## 🛠️ 1) Starter Pack (Prasyarat)

Sebelum lu nge-gas, pastiin spek perangkat lu udah *up-to-date* dan gak bikin *rehab*:
* ☕ **Java 17** (wajib hukumnya, jangan pake yang purba)
* 🏗️ **Maven 3.8+** (buat ngurusin dependencies biar ga *error*)
* 🐬 **XAMPP** (minimal komponen **MySQL** di-install, jan nyari yang lain)

---

## 🚀 2) Nyalain MySQL di XAMPP

1. Buka aplikasi **XAMPP Control Panel** lu yang legendaris itu.
2. Klik tombol **Start** di sebelah service **MySQL**.
3. Pastiin statusnya berubah jadi hijau alias <kbd>Running</kbd>. Kalau warna kuning atau merah, tarik napas dalam-dalam, kita selesaikan di bawah.

---

## 🗄️ 3) Setup Database (No Ribet-Ribet Club)

Ini konfigurasi default koneksi kita:
* **Host:** `localhost`
* **Port:** `3306`
* **DB Name:** `superkosdb`
* **User:** `root`
* **Password:** *(kosongin aja kayak status lu)*

Masuk ke **phpMyAdmin** (`http://localhost/phpmyadmin`) atau MySQL client favorit lu (DBeaver/Navicat), terus tinggal *copas* dan run query super simpel ini:

```sql
CREATE DATABASE IF NOT EXISTS superkosdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE superkosdb;

```

> 💡 **INFO PENTING:** Lu **GAK PERLU** bikin tabel manual sampai pusing tujuh keliling. Aplikasi ini udah pake sihir `spring.jpa.hibernate.ddl-auto=update`, jadi tabel bakal auto-generate sendiri pas aplikasi pertama kali dinyalain! *Magical kan?* ✨

---

## ⚙️ 4) Double Check Konfigurasi

Coba intip dulu file `src/main/resources/application.properties`. Pastiin isi dalemannya udah se-akurat ini:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/superkosdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
server.port=8080

```

*Notes: Kalau MySQL lu pake password root, langsung isi aja di bagian `spring.datasource.password=` ya, jangan dikosongin!*

---

## 🏃‍♂️ 5) Jalankan Aplikasi (Let's Go!)

Buka terminal/command prompt lu, arahin ke *root project* (folder tempat si `pom.xml` berada), terus langsung eksekusi command sakti ini:

```bash
mvn spring-boot:run

```

**Atau** kalau lu tim *clean-build* dulu baru jalanin file JAR-nya, pake cara ini:

```bash
mvn clean package
java -jar target/superkos-backend-0.0.1-SNAPSHOT.jar

```

---

## 🌐 6) Akses Aplikasi

Kalau log di terminal udah tenang dan ga ada teks merah ngamuk, langsung buka browser andalan lu ke URL ini:

🔗 [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080)

---

## 👥 7) Akun Awal buat Testing (Auto-Seeded)

Tenang, data dummy udah otomatis masuk pas aplikasi nge-start. Lu tinggal *login* pake akun-akun di bawah ini buat nyoba *flow*-nya:

| Role 🎭 | Email / Username 📧 | Password 🔑 |
| --- | --- | --- |
| **Admin** | `admin` | `admin12345` |
| **Pemilik Properti** | `pemilik@superkos.com` | `pemilik123` |
| **Pencari Hunian** | `ahmad@superkos.com` | `ahmad123` |

---

## 🚨 8) Troubleshooting (Anti Overthinking)

Pas nyoba malah nemu *error*? Gak usah langsung pengen *healing*, ini solusinya:

* **🛑 Port 3306 Bentrok / MySQL Gagal Start**
* *Solusi:* Biasanya ada MySQL bawaan OS yang udah jalan. Ubah port MySQL di XAMPP lu, terus samain port-nya di baris `spring.datasource.url` pada file properti tadi.


* **🔒 Akses Ditolak buat User 'root'**
* *Solusi:* Cek lagi username ama password di `application.properties`. Pastiin gak ada typo sekecil apapun.


* **🚫 Port 8080 Udah Dipake Aplikasi Lain**
* *Solusi:* Tinggal ganti aja `server.port` nya (misal jadi `8081` atau `8082`) di `application.properties`.



---
