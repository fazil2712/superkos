<div align="center">
<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=700&size=28&duration=2000&pause=500&color=FF5733&center=true&vCenter=true&width=500&lines=%F0%9F%9A%80+Welcome+to+Superkos%EF%B8%8F!;%F0%9F%9A%80+Aplikasi+Cari+Kost%EF%B8%8F!;%E2%9C%A8+Paling+Kece%E2%9C%A8" alt="Typing SVG" />
</p>
</div>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Thymeleaf-Template-blue?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf" />
  <img src="https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/Tomcat-9-yellow?style=for-the-badge&logo=apache-tomcat&logoColor=white" alt="Tomcat 9" />
</p>

---

## 🤔 POV: Apa itu Superkos?
**Superkos** itu *literally* sistem *backend* kece buat pencarian hunian kos-kosan yang dibangun pakai **Spring Boot Web** dan **Thymeleaf Template Engine**[cite: 1]. Project ini dibikin buat bantu anak rantau nyari kosan impian dengan *effort* minimal[cite: 1].

**✨ Update Fitur yang Udah *Done* (Valid No Debat):**
*   **Database Hunian:** Data kosan udah *ready* di dalam sistem[cite: 1].
*   **Smart Search:** Bisa nyari kosan langsung sat-set[cite: 1].
*   **Filter & Sort:** Gampang banget buat milih hunian sesuai *budget* dan kriteria[cite: 1].
*   **Login & Registrasi:** Udah jalan lancar jaya buat *User* biasa[cite: 1]. Buat *role* Admin dan Seller masih *on going* ya ngab, tungguin aja![cite: 1].

---

## 🛠️ Starter Pack (Prasyarat)
Sebelum nge-gas buat *running* project-nya, pastiin laptop lu udah *install* *starter pack* ini biar gak kena *red flag*:
*   ☕ **Java 17**: Wajib banget dipakai buat *environment* utama[cite: 1].
*   🏗️ **Maven 3.9**: Harus *install* versi ini dan jangan lupa *set environmental variables* di PC/Laptop ente[cite: 1].
*   🐱 **Tomcat 9**: Versi 9 ini hukumnya wajib ya, gak nerima nego![cite: 1].
*   🐬 **XAMPP**: Minimal ada komponen **MySQL** dan **Apache** yang bisa di-*run*[cite: 1].

---

## 🚀 Step-by-Step Deployment Lokal

### 1️⃣ Nyalain Mesin Database (XAMPP)
1. Buka **XAMPP Control Panel** lu[cite: 1].
2. Klik tombol **Start** di sebelah *service* **Apache** dan **MySQL**[cite: 1].
3. Pastiin statusnya udah warna ijo alias **Running**[cite: 1].

### 2️⃣ Siapin Database (No Ribet Club)
Kita pakai konfigurasi koneksi *default* kayak gini:
*   **Host:** `localhost`[cite: 1]
*   **Port:** `3306`[cite: 1]
*   **DB:** `superkosdb`[cite: 1]
*   **User:** `root`[cite: 1]
*   **Password:** *(kosongin aja kayak status lu)*[cite: 1]

Masuk ke `phpMyAdmin` atau *MySQL client* andalan lu, terus jalanin *query* SQL ini[cite: 1]:
```sql
CREATE DATABASE IF NOT EXISTS superkosdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE superkosdb;

```

> **💡 FYI:** Lu **GAK PERLU** repot bikin tabel manual dari awal. Aplikasi bakal otomatis nge-*create* atau *update* tabel pas pertama kali di-run berkat sihir `spring.jpa.hibernate.ddl-auto=update`.
> 
> 

### 3️⃣ Cek Konfigurasi Aplikasi (Spill The T)

Coba intip file `src/main/resources/application.properties` dan pastiin isinya udah valid kayak gini:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/superkosdb?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
server.port=8080

```

(Kalau MySQL lu pake password root, tinggal isi aja bagian `spring.datasource.password`-nya.)

### 4️⃣ Jalankan Aplikasi (Let's Go!)

Buka terminal/CMD lu, arahin ke *root project* (folder yang ada file `pom.xml`-nya). Terus, tinggal *run command* sakti ini:

```bash
mvn clean spring-boot:run

```

*(Atau kalau lu tim nge-build dulu, bisa pake `mvn clean package` trus `java -jar target/superkos-backend-0.0.1-SNAPSHOT.jar`)*.

### 5️⃣ Akses Webnya

Kalau udah sukses *running* tanpa pesan *error* warna merah, langsung aja buka *browser* lu ke:
👉 **[http://localhost:8080](http://localhost:8080)**

---

## 🎫 Akun VIP buat Testing

Biar lu gak pusing, data *dummy* udah otomatis di-*seed* pas aplikasi jalan. Tinggal *login* pakai akun-akun *privilege* ini:

| Role 🎭 | Email / Username 📧 | Password 🔑 |
| --- | --- | --- |
| **Admin** | `admin`<br> | `admin12345`<br> |
| **Pemilik Properti** | `pemilik@superkos.com`<br> | `pemilik123`<br> |
| **Pencari Hunian** | `ahmad@superkos.com`<br> | `ahmad123`<br> |

---

## 🚨 Q&A & Troubleshooting (Biar Gak Overthinking)

> ⚠️ **WARNING KERAS:** Beres kan? Tinggal run webnya :D. **Yg ga baca tapi nanya gw cium** 💋.
> 
>
