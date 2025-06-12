# FilmHub
Final Lab. Pemrograman Mobile 

# Deskripsi Aplikasi
FilmHub adalah aplikasi yang menampilkan katalog film, bukan cuman sebatas itu, aplikasi ini juga bisa menjadi wadah untuk para penikmat film untuk menilai (Rating dan Review) berdasarkan film-film yang mereka sukai. Kelebihan dari aplikasi ini adalah statistik film seperti jumlah film yang ditonton, rating pribadi, dan lain sebagainya. Aplikasi ini bisa tema gelap/terang untuk kenyamanan usernya. 

# Cara Penggunaan
User masuk ke aplikasi dan disuguhkan tampilan aplikasi seperti: 

## Halaman Home 
- User ditampilkan oleh katalog film, di bagian sini user bisa scrool ke bawah untuk mencari film yang mereka sukai, bisa juga user mencari film dengan fitur searching, filter genre (multi-select), dan sorting berdasarkan paling populer, rating tertinggi, dll
- User bisa meng-klik salah satu film dan diarahkan ke activity baru, yaitu 'Detail Film'. Disini user bisa melihat detail film seperti poster, judul film, genre, durasi, rating, tanggal rilis, dan Sinopsis. Ada juga dua tombol disini yaitu 'Tambah ke Favorit' dan 'Tandai Sudah Ditonton'. 

## Halaman Detail Film
Ketika user menekan tombol 'Tandai sudah ditonton', maka akan muncul pop up untuk review film tersebut, seperti rating dan juga komentar. Ada juga tombol untuk simpan dan batal. 

## Halaman Favorite
Disini User bisa melihat daftar film yang mereka favoritkan, dan ketika mereka klik salah satu film card dari film tersebut, ada pilihan untuk 'Hapus dari Favorite' dalam bentuk toggle. Disini juga user bisa mengedit catatan atau review yang mereka buat, seperti mengubah rating dan komentar.

## Halaman Analytics 
- Disini user bisa melihat statistik film yang mereka sudah tonton atau review, seperti jumlah film yang mereka tonton, total durasi, rata-rata rating pribadi dan top 2 genre. 
- Di bagian riwayat tontonan, user bisa melihat film-film apa saja yang mereka sudah review dan bisa di scrool. Di sini juga user bisa menghapus riwayat tontonan mereka dan ketika dihapus, statistik film akan berkurang. 

# Implementasi Teknis
- Data menggunakan API imdb.
