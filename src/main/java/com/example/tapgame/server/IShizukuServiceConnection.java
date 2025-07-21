package com.example.tapgame.server; import android.os.IInterface; public interface IShizukuServiceConnection extends IInterface { void connected(android.os.IBinder binder); void died(); }
