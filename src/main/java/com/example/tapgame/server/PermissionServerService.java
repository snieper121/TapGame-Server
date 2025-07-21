package com.example.tapgame.server;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import java.util.Map;

/**
 * Сервис для управления разрешениями
 * Обеспечивает персистентное хранение разрешений после отключения WiFi отладки
 */
public class PermissionServerService extends Service {
    private static final String TAG = "PermissionServerService";
    private static final String PREFS_NAME = "permission_server_prefs";
    private static final String KEY_PERMISSIONS = "permissions_";
    
    private SharedPreferences sharedPreferences;
    private boolean isActive = true;
    
    private final IMyPermissionServer.Stub binder = new IMyPermissionServer.Stub() {
        
        @Override
        public boolean savePermission(String permission, boolean granted) throws RemoteException {
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(KEY_PERMISSIONS + permission, granted);
                boolean result = editor.commit();
                Log.d(TAG, "Saved permission: " + permission + " = " + granted + ", result: " + result);
                return result;
            } catch (Exception e) {
                Log.e(TAG, "Failed to save permission: " + permission, e);
                return false;
            }
        }
        
        @Override
        public boolean getPermission(String permission) throws RemoteException {
            try {
                boolean result = sharedPreferences.getBoolean(KEY_PERMISSIONS + permission, false);
                Log.d(TAG, "Get permission: " + permission + " = " + result);
                return result;
            } catch (Exception e) {
                Log.e(TAG, "Failed to get permission: " + permission, e);
                return false;
            }
        }
        
        @Override
        public Bundle getAllPermissions() throws RemoteException {
            try {
                Bundle bundle = new Bundle();
                Map<String, ?> allPrefs = sharedPreferences.getAll();
                
                for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(KEY_PERMISSIONS)) {
                        String permission = key.substring(KEY_PERMISSIONS.length());
                        boolean value = (Boolean) entry.getValue();
                        bundle.putBoolean(permission, value);
                    }
                }
                
                Log.d(TAG, "Retrieved " + bundle.size() + " permissions");
                return bundle;
            } catch (Exception e) {
                Log.e(TAG, "Failed to get all permissions", e);
                return new Bundle();
            }
        }
        
        @Override
        public boolean clearAllPermissions() throws RemoteException {
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Map<String, ?> allPrefs = sharedPreferences.getAll();
                
                for (String key : allPrefs.keySet()) {
                    if (key.startsWith(KEY_PERMISSIONS)) {
                        editor.remove(key);
                    }
                }
                
                boolean result = editor.commit();
                Log.d(TAG, "Cleared all permissions, result: " + result);
                return result;
            } catch (Exception e) {
                Log.e(TAG, "Failed to clear all permissions", e);
                return false;
            }
        }
        
        @Override
        public boolean isActive() throws RemoteException {
            return isActive;
        }
        
        @Override
        public boolean isPermissionSaved() throws RemoteException {
            // Проверяем, есть ли хотя бы одно сохраненное разрешение
            try {
                Map<String, ?> allPrefs = sharedPreferences.getAll();
                for (String key : allPrefs.keySet()) {
                    if (key.startsWith(KEY_PERMISSIONS)) {
                        Boolean value = (Boolean) allPrefs.get(key);
                        if (value != null && value) {
                            return true;
                        }
                    }
                }
                return false;
            } catch (Exception e) {
                Log.e(TAG, "Failed to check if permission saved", e);
                return false;
            }
        }
        
        @Override
        public boolean isPermissionActive() throws RemoteException {
            // Здесь можно добавить логику проверки активного ADB соединения
            // Пока возвращаем состояние сервиса
            return isActive;
        }
        
        @Override
        public void setPermissionSaved(boolean saved) throws RemoteException {
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("permission_saved_status", saved);
                editor.commit();
                Log.d(TAG, "Set permission saved status: " + saved);
            } catch (Exception e) {
                Log.e(TAG, "Failed to set permission saved status", e);
            }
        }
        
        @Override
        public boolean isShizukuActive() throws RemoteException {
            // Здесь можно добавить логику проверки Shizuku
            // Пока возвращаем false
            return false;
        }
        
        @Override
        public void requestShizukuPermission() throws RemoteException {
            // Здесь можно добавить логику запроса разрешения Shizuku
            Log.d(TAG, "Shizuku permission requested");
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Log.d(TAG, "PermissionServerService created");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound");
        return binder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "Service unbound");
        return super.onUnbind(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        isActive = false;
        Log.d(TAG, "PermissionServerService destroyed");
    }
}

