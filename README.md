# Dicoding Story App (Submission 1)

Project ini dibuat untuk memenuhi kriteria Submission **Belajar Pengembangan Aplikasi Android Intermediate** (Story App).

## Fitur yang dipenuhi (Wajib)
- Halaman autentikasi: Login & Register
- Custom View EditText:
  - Password: error realtime jika < 8 karakter
  - Email (opsional): error realtime jika format email tidak valid
- Session & Token tersimpan di **DataStore Preferences**
- Logout (menu: `R.id.action_logout`) menghapus session & token
- List Story dari API (RecyclerView)
- Detail Story (klik item)
- Add Story (Gallery wajib + Camera opsional), upload multipart ke API
- Loading + Error info pada semua proses API

## Animasi (Wajib)
Jenis animasi: **Property Animation**  
Lokasi: `WelcomeActivity#playAnimation()` (fade-in bertahap untuk hero image, title, desc, dan tombol).

## Catatan
- Endpoint API: https://story-api.dicoding.dev/v1/
- Batas upload foto: max 1 MB (di-handle dengan `reduceFileImage()`)

Silakan lihat file `STEP_BY_STEP.md` untuk panduan lengkap menjalankan project.
