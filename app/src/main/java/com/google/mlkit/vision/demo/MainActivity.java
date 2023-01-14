/*
package com.google.mlkit.vision.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import com.google.mlkit.vision.demo.java.ChooserActivity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EntryChoiceActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {

    private static final String TAG = "EntryChoiceActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private static final String[] REQUIRED_RUNTIME_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};
    @NotNull
    public static final EntryChoiceActivity.Companion Companion = new EntryChoiceActivity.Companion((DefaultConstructorMarker)null);
    private HashMap _$_findViewCache;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_vision_entry_choice);
        ((TextView)this.findViewById(R.id.java_entry_point)).setOnClickListener((OnClickListener)(new OnClickListener() {
            public final void onClick(View it) {
                Intent intent = new Intent((Context)EntryChoiceActivity.this, ChooserActivity.class);
                EntryChoiceActivity.this.startActivity(intent);
            }
        }));
        if (!this.allRuntimePermissionsGranted()) {
            this.getRuntimePermissions();
        }

    }

    private final boolean allRuntimePermissionsGranted() {
        String[] var3 = REQUIRED_RUNTIME_PERMISSIONS;
        int var4 = var3.length;

        for(int var2 = 0; var2 < var4; ++var2) {
            String permission = var3[var2];
            if (permission != null) {
                int var7 = false;
                if (!this.isPermissionGranted((Context)this, permission)) {
                    return false;
                }
            }
        }

        return true;
    }

    private final void getRuntimePermissions() {
        ArrayList permissionsToRequest = new ArrayList();
        String[] var4 = REQUIRED_RUNTIME_PERMISSIONS;
        int var5 = var4.length;

        for(int var3 = 0; var3 < var5; ++var3) {
            String permission = var4[var3];
            if (permission != null) {
                int var8 = false;
                if (!this.isPermissionGranted((Context)this, permission)) {
                    permissionsToRequest.add(permission);
                }
            }
        }

        Collection $this$toTypedArray$iv = (Collection)permissionsToRequest;
        if (!$this$toTypedArray$iv.isEmpty()) {
            Activity var10000 = (Activity)this;
            $this$toTypedArray$iv = (Collection)permissionsToRequest;
            int $i$f$toTypedArray = false;
            Object[] var10001 = $this$toTypedArray$iv.toArray(new String[0]);
            Intrinsics.checkNotNull(var10001, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
            ActivityCompat.requestPermissions(var10000, (String[])var10001, 1);
        }

    }

    private final boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == 0) {
            Log.i("EntryChoiceActivity", "Permission granted: " + permission);
            return true;
        } else {
            Log.i("EntryChoiceActivity", "Permission NOT granted: " + permission);
            return false;
        }
    }

    public View _$_findCachedViewById(int var1) {
        if (this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View)this._$_findViewCache.get(var1);
        if (var2 == null) {
            var2 = this.findViewById(var1);
            this._$_findViewCache.put(var1, var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if (this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }

    @Metadata(
            mv = {1, 7, 0},
            k = 1,
            xi = 2,
            d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\bR\u000e\u0010\t\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000¨\u0006\n"},
            d2 = {"Lcom/google/mlkit/vision/demo/EntryChoiceActivity$Companion;", "", "()V", "PERMISSION_REQUESTS", "", "REQUIRED_RUNTIME_PERMISSIONS", "", "", "[Ljava/lang/String;", "TAG", "ML_Kit_Vision_Quickstart.app.main"}
    )
    public static final class Companion {
        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}
*/
