package io.github.pigeonmuyz.jx3bot.entity;

public class VersionFix {
    String old_version;
    String new_version;
    Integer package_num;
    String package_size;

    public String getOld_version() {
        return old_version;
    }

    public void setOld_version(String old_version) {
        this.old_version = old_version;
    }

    public String getNew_version() {
        return new_version;
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
    }

    public Integer getPackage_num() {
        return package_num;
    }

    public void setPackage_num(Integer package_num) {
        this.package_num = package_num;
    }

    public String getPackage_size() {
        return package_size;
    }

    public void setPackage_size(String package_size) {
        this.package_size = package_size;
    }
}

