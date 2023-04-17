package com.example.pelaporank3ft.Model;

public class LaporInsidensModel {

    String kode_laporinsiden, tanda_pengenal_insiden, waktu_kejadian_insiden, lokasi_departemen_insiden,
            lokasi_rinci_insiden, jenis_insiden, kronologi_insiden, penyebab_insiden, nama_pelapor_insiden,
            email_pelapor_insiden, nomor_telepon_pelapor_insiden, unit_pelapor_insiden, nama_korban_insiden,
            email_korban_insiden, nomer_telepon_korban_insiden, unit_korban_insiden, gambar_insiden,
            dibuat_insiden, diupdate_insiden, id_user, status_laporan_insiden, kategori_insiden,
            tenggat_waktu_insiden, tindakan_insiden, id_p2k3, nama_p2k3;

    Boolean status_deleted_insiden;

    public LaporInsidensModel() {
    }

    public LaporInsidensModel(String kode_laporinsiden, String tanda_pengenal_insiden,
                              String waktu_kejadian_insiden, String lokasi_departemen_insiden,
                              String lokasi_rinci_insiden, String jenis_insiden, String kronologi_insiden,
                              String penyebab_insiden, String nama_pelapor_insiden,
                              String email_pelapor_insiden, String nomor_telepon_pelapor_insiden,
                              String unit_pelapor_insiden, String nama_korban_insiden,
                              String email_korban_insiden, String nomer_telepon_korban_insiden,
                              String unit_korban_insiden, String gambar_insiden, String dibuat_insiden,
                              String diupdate_insiden, String id_user, String status_laporan_insiden,
                              String kategori_insiden, String tenggat_waktu_insiden, String tindakan_insiden,
                              String id_p2k3, String nama_p2k3, Boolean status_deleted_insiden) {
        this.kode_laporinsiden = kode_laporinsiden;
        this.tanda_pengenal_insiden = tanda_pengenal_insiden;
        this.waktu_kejadian_insiden = waktu_kejadian_insiden;
        this.lokasi_departemen_insiden = lokasi_departemen_insiden;
        this.lokasi_rinci_insiden = lokasi_rinci_insiden;
        this.jenis_insiden = jenis_insiden;
        this.kronologi_insiden = kronologi_insiden;
        this.penyebab_insiden = penyebab_insiden;
        this.nama_pelapor_insiden = nama_pelapor_insiden;
        this.email_pelapor_insiden = email_pelapor_insiden;
        this.nomor_telepon_pelapor_insiden = nomor_telepon_pelapor_insiden;
        this.unit_pelapor_insiden = unit_pelapor_insiden;
        this.nama_korban_insiden = nama_korban_insiden;
        this.email_korban_insiden = email_korban_insiden;
        this.nomer_telepon_korban_insiden = nomer_telepon_korban_insiden;
        this.unit_korban_insiden = unit_korban_insiden;
        this.gambar_insiden = gambar_insiden;
        this.dibuat_insiden = dibuat_insiden;
        this.diupdate_insiden = diupdate_insiden;
        this.id_user = id_user;
        this.status_laporan_insiden = status_laporan_insiden;
        this.kategori_insiden = kategori_insiden;
        this.tenggat_waktu_insiden = tenggat_waktu_insiden;
        this.tindakan_insiden = tindakan_insiden;
        this.id_p2k3 = id_p2k3;
        this.nama_p2k3 = nama_p2k3;
        this.status_deleted_insiden = status_deleted_insiden;
    }

    public String getKode_laporinsiden() {
        return kode_laporinsiden;
    }

    public void setKode_laporinsiden(String kode_laporinsiden) {
        this.kode_laporinsiden = kode_laporinsiden;
    }

    public String getTanda_pengenal_insiden() {
        return tanda_pengenal_insiden;
    }

    public void setTanda_pengenal_insiden(String tanda_pengenal_insiden) {
        this.tanda_pengenal_insiden = tanda_pengenal_insiden;
    }

    public String getWaktu_kejadian_insiden() {
        return waktu_kejadian_insiden;
    }

    public void setWaktu_kejadian_insiden(String waktu_kejadian_insiden) {
        this.waktu_kejadian_insiden = waktu_kejadian_insiden;
    }

    public String getLokasi_departemen_insiden() {
        return lokasi_departemen_insiden;
    }

    public void setLokasi_departemen_insiden(String lokasi_departemen_insiden) {
        this.lokasi_departemen_insiden = lokasi_departemen_insiden;
    }

    public String getLokasi_rinci_insiden() {
        return lokasi_rinci_insiden;
    }

    public void setLokasi_rinci_insiden(String lokasi_rinci_insiden) {
        this.lokasi_rinci_insiden = lokasi_rinci_insiden;
    }

    public String getJenis_insiden() {
        return jenis_insiden;
    }

    public void setJenis_insiden(String jenis_insiden) {
        this.jenis_insiden = jenis_insiden;
    }

    public String getKronologi_insiden() {
        return kronologi_insiden;
    }

    public void setKronologi_insiden(String kronologi_insiden) {
        this.kronologi_insiden = kronologi_insiden;
    }

    public String getPenyebab_insiden() {
        return penyebab_insiden;
    }

    public void setPenyebab_insiden(String penyebab_insiden) {
        this.penyebab_insiden = penyebab_insiden;
    }

    public String getNama_pelapor_insiden() {
        return nama_pelapor_insiden;
    }

    public void setNama_pelapor_insiden(String nama_pelapor_insiden) {
        this.nama_pelapor_insiden = nama_pelapor_insiden;
    }

    public String getEmail_pelapor_insiden() {
        return email_pelapor_insiden;
    }

    public void setEmail_pelapor_insiden(String email_pelapor_insiden) {
        this.email_pelapor_insiden = email_pelapor_insiden;
    }

    public String getNomor_telepon_pelapor_insiden() {
        return nomor_telepon_pelapor_insiden;
    }

    public void setNomor_telepon_pelapor_insiden(String nomor_telepon_pelapor_insiden) {
        this.nomor_telepon_pelapor_insiden = nomor_telepon_pelapor_insiden;
    }

    public String getUnit_pelapor_insiden() {
        return unit_pelapor_insiden;
    }

    public void setUnit_pelapor_insiden(String unit_pelapor_insiden) {
        this.unit_pelapor_insiden = unit_pelapor_insiden;
    }

    public String getNama_korban_insiden() {
        return nama_korban_insiden;
    }

    public void setNama_korban_insiden(String nama_korban_insiden) {
        this.nama_korban_insiden = nama_korban_insiden;
    }

    public String getEmail_korban_insiden() {
        return email_korban_insiden;
    }

    public void setEmail_korban_insiden(String email_korban_insiden) {
        this.email_korban_insiden = email_korban_insiden;
    }

    public String getNomer_telepon_korban_insiden() {
        return nomer_telepon_korban_insiden;
    }

    public void setNomer_telepon_korban_insiden(String nomer_telepon_korban_insiden) {
        this.nomer_telepon_korban_insiden = nomer_telepon_korban_insiden;
    }

    public String getUnit_korban_insiden() {
        return unit_korban_insiden;
    }

    public void setUnit_korban_insiden(String unit_korban_insiden) {
        this.unit_korban_insiden = unit_korban_insiden;
    }

    public String getGambar_insiden() {
        return gambar_insiden;
    }

    public void setGambar_insiden(String gambar_insiden) {
        this.gambar_insiden = gambar_insiden;
    }

    public String getDibuat_insiden() {
        return dibuat_insiden;
    }

    public void setDibuat_insiden(String dibuat_insiden) {
        this.dibuat_insiden = dibuat_insiden;
    }

    public String getDiupdate_insiden() {
        return diupdate_insiden;
    }

    public void setDiupdate_insiden(String diupdate_insiden) {
        this.diupdate_insiden = diupdate_insiden;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getStatus_laporan_insiden() {
        return status_laporan_insiden;
    }

    public void setStatus_laporan_insiden(String status_laporan_insiden) {
        this.status_laporan_insiden = status_laporan_insiden;
    }

    public String getKategori_insiden() {
        return kategori_insiden;
    }

    public void setKategori_insiden(String kategori_insiden) {
        this.kategori_insiden = kategori_insiden;
    }

    public String getTenggat_waktu_insiden() {
        return tenggat_waktu_insiden;
    }

    public void setTenggat_waktu_insiden(String tenggat_waktu_insiden) {
        this.tenggat_waktu_insiden = tenggat_waktu_insiden;
    }

    public String getTindakan_insiden() {
        return tindakan_insiden;
    }

    public void setTindakan_insiden(String tindakan_insiden) {
        this.tindakan_insiden = tindakan_insiden;
    }

    public String getId_p2k3() {
        return id_p2k3;
    }

    public void setId_p2k3(String id_p2k3) {
        this.id_p2k3 = id_p2k3;
    }

    public String getNama_p2k3() {
        return nama_p2k3;
    }

    public void setNama_p2k3(String nama_p2k3) {
        this.nama_p2k3 = nama_p2k3;
    }

    public Boolean getStatus_deleted_insiden() {
        return status_deleted_insiden;
    }

    public void setStatus_deleted_insiden(Boolean status_deleted_insiden) {
        this.status_deleted_insiden = status_deleted_insiden;
    }
}