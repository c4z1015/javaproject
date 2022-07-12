package org.czb.xingcan.db.domain;

import lombok.Data;

import java.util.List;

@Data
public class OrderConfirm {
    private UserAddress userAddress;
    private List<Store> stores;

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }
}
