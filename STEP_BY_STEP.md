# Step-by-step (Sangat Detail)

## 0) Prasyarat
1. Android Studio terbaru (direkomendasikan Stable).
2. JDK 17 (biasanya sudah dibundel Android Studio).
3. Emulator atau HP Android (minimal Android 5.0 / API 21).
4. Internet aktif (karena akses API Dicoding Story).

---

## 1) Cara membuka project
1. Extract ZIP project.
2. Buka Android Studio
3. Pilih **Open** → arahkan ke folder `DicodingStoryApp`
4. Tunggu Gradle Sync selesai (biasanya akan download dependency)

> Jika Android Studio meminta upgrade Gradle/AGP, boleh di-accept (umumnya aman).

---

## 2) Menjalankan aplikasi
1. Pilih device emulator/HP.
2. Klik tombol **Run** (▶).
3. Aplikasi akan masuk ke **WelcomeActivity**.

---

## 3) Alur aplikasi (sesuai kriteria)
### A. Session (Login State)
- Saat aplikasi dibuka:
  - Jika sudah login (token tersimpan), langsung menuju `MainActivity`.
  - Jika belum login, tetap di `WelcomeActivity`.

Lokasi kode:
- `WelcomeActivity` mengamati session: `WelcomeViewModel.getSession()`
- Session disimpan di DataStore: `UserPreference`

---

### B. Register
1. Dari Welcome → tekan **Daftar**.
2. Isi:
   - Nama (`R.id.ed_register_name`)
   - Email (`R.id.ed_register_email`)
   - Password (`R.id.ed_register_password`) → otomatis tersembunyi dan validasi realtime
3. Tekan tombol **Daftar**
4. Jika sukses, diarahkan ke Login.

Lokasi kode:
- UI: `RegisterActivity`
- API: `ApiService.register()`
- Repository: `StoryRepository.register()`

---

### C. Login
1. Dari Welcome → tekan **Masuk**
2. Isi:
   - Email (`R.id.ed_login_email`)
   - Password (`R.id.ed_login_password`)
3. Tekan **Masuk**
4. Jika sukses:
   - token + name disimpan ke DataStore
   - diarahkan ke `MainActivity` dengan flags `NEW_TASK | CLEAR_TASK` (sesuai saran back behavior)

Lokasi kode:
- UI: `LoginActivity`
- Simpan session: `AuthViewModel.saveSession()`

---

### D. List Story (MainActivity)
1. Setelah login, aplikasi menampilkan daftar story.
2. Minimal yang ditampilkan:
   - Nama user (`R.id.tv_item_name`)
   - Foto (`R.id.iv_item_photo`)
3. Ada pull-to-refresh.

Lokasi kode:
- UI: `MainActivity`
- Adapter: `StoryAdapter`
- API: `ApiService.getStories()`

---

### E. Detail Story
1. Klik item story.
2. Menampilkan minimal:
   - Nama (`R.id.tv_detail_name`)
   - Foto (`R.id.iv_detail_photo`)
   - Deskripsi (`R.id.tv_detail_description`)

Lokasi kode:
- UI: `DetailActivity`

---

### F. Add Story (Tambah Cerita)
1. Dari `MainActivity` tekan FAB (+)
2. Pilih foto:
   - Gallery (wajib) → tombol **Gallery**
   - Camera (opsional) → tombol **Camera**
3. Isi deskripsi (`R.id.ed_add_description`)
4. Tekan tombol upload (`R.id.button_add`)
5. Jika sukses → kembali ke `MainActivity` dan list akan memuat data terbaru.

Lokasi kode:
- UI: `AddStoryActivity`
- Kompres <= 1MB: `utils/reduceFileImage()`
- API: `ApiService.uploadStory()`

---

### G. Logout
1. Di `MainActivity`, buka menu (titik tiga) → Logout (`R.id.action_logout`)
2. Token dan session dihapus dari DataStore.
3. Kembali ke `WelcomeActivity` dan task dibersihkan.

Lokasi kode:
- `MainActivity.onOptionsItemSelected()`
- `MainViewModel.logout()`
- `UserPreference.logout()`

---

## 4) Troubleshooting umum
### A. Gradle Sync lama / gagal
- Pastikan internet stabil.
- Coba: File → Invalidate Caches / Restart.

### B. Upload gagal (400 / 413 / dll)
- Pastikan ukuran foto tidak besar (project sudah kompres sampai <= 1MB).
- Coba foto lain.

### C. Error "Email is already taken"
- Gunakan email lain saat register (API Dicoding menolak email duplikat).

---

## 5) Checklist Kriteria Wajib (cepat)
- [x] Login page + Register page sesuai ID
- [x] Password hidden + validasi realtime via custom view
- [x] Session & token disimpan DataStore
- [x] Logout remember clear token
- [x] List story tampil (nama + foto)
- [x] Detail story tampil (nama + foto + deskripsi)
- [x] Add story dari gallery + upload berhasil + kembali ke list
- [x] Ada animasi property di WelcomeActivity
