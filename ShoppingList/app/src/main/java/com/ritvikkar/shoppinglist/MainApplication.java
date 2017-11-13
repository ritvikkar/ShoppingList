package com.ritvikkar.shoppinglist;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainApplication  extends Application {

    private Realm realmItems;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }

    public void openRealm() {
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realmItems = Realm.getInstance(config);
    }

    public void closeRealm() {
        realmItems.close();
    }

    public Realm getRealmItems() {
        return realmItems;
    }
}
