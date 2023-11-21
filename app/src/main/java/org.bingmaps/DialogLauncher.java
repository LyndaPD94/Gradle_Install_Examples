package org.bingmaps.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.bingmaps.app.bsds.Record;
import org.bingmaps.app.sdk.BingMapsView;
import org.bingmaps.app.sdk.TileLayer;
import org.bingmaps.app.sdk.Utilities;

import java.util.HashMap;
import java.util.Objects;

import lyn.bee.hive_record_app.R;

public class DialogLauncher {

    public static void LaunchAboutDialog(final Activity activity) {
        final View aboutView = activity.getLayoutInflater().inflate(R.layout.about, (ViewGroup) activity.findViewById(R.id.aboutView));

        AlertDialog.Builder aboutAlert = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.about))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(aboutView)
                .setPositiveButton(activity.getString(R.string.ok), (dialog, whichButton) -> {
                    // Canceled. Do nothing
                });
        aboutAlert.show();
    }

    public static void LaunchLayersDialog(final Activity activity, final BingMapsView bingMapsView, final CharSequence[] dataLayers, boolean[] dataLayerSelections) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_menu_slideshow)
                .setTitle(activity.getString(R.string.layers))
                .setMultiChoiceItems(dataLayers, dataLayerSelections, (arg0, idx, isChecked) -> {
                    if (dataLayers[idx] == activity.getString(R.string.traffic)) {
                        if (isChecked) {
                            bingMapsView.getLayerManager().addLayer(new TileLayer(activity.getString(R.string.traffic), org.bingmaps.app.sdk.Constants.TrafficTileLayerURI, 0.5));
                        } else {
                            bingMapsView.getLayerManager().clearLayer(activity.getString(R.string.traffic));
                        }
                    }
                    //Add support for more map data layers here
                })
                .setPositiveButton(activity.getString(R.string.close), (arg0, arg1) -> {
                })
                .show();
    }

    @SuppressLint("SetTextI18n")
    public static void LaunchEntityDetailsDialog(final Activity activity, final HashMap<String, Object> metadata) {
        if (metadata.size() > 0) {
            if (metadata.containsKey("record") && Objects.requireNonNull(metadata.get("record")).getClass() == Record.class) {
                Record record = (Record) metadata.get("record");

                String title = Utilities.isNullOrEmpty(Objects.requireNonNull(record).DisplayName) ?
                        activity.getString(R.string.details) : record.DisplayName;

                final ScrollView detailsView = (ScrollView) activity.getLayoutInflater().inflate(R.layout.details_view, (ViewGroup) activity.findViewById(R.id.detailsView));

                if (record.Address != null) {
                    TextView addressView = (TextView) detailsView.findViewById(R.id.detailsAddress);
                    addressView.setText(activity.getString(R.string.address) + record.Address.toString());
                }

                if (!Utilities.isNullOrEmpty(record.Phone)) {
                    final String phone = "tel:" + record.Phone;

                    ImageButton phoneBtn = (ImageButton) detailsView.findViewById(R.id.detailsPhoneBtn);
                    phoneBtn.setOnClickListener(v -> {
                        //Call phone number
                        Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(phone));
                        activity.startActivity(i);
                    });
                    phoneBtn.setVisibility(View.VISIBLE);
                }

                //OPTION Add custom content to view
                LinearLayout ccView = (LinearLayout) detailsView.findViewById(R.id.detailsCustomContent);

                if (record.Metadata.containsKey("Manager")) {
                    String manager = (String) record.Metadata.get("Manager");
                    if (!Utilities.isNullOrEmpty(manager)) {
                        TextView managerView = new TextView(activity);
                        managerView.setText("Manager: " + manager);
                        ccView.addView(managerView);
                    }
                }

                if (record.Metadata.containsKey("StoreType")) {
                    String storeType = (String) record.Metadata.get("StoreType");
                    if (!Utilities.isNullOrEmpty(storeType)) {
                        TextView storeTypeView = new TextView(activity);
                        storeTypeView.setText("Store Type: " + storeType);
                        ccView.addView(storeTypeView);
                    }
                }

                AlertDialog.Builder detailsAlert = new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setView(detailsView)
                        .setNegativeButton(activity.getString(R.string.close), (dialog, whichButton) -> {
                            // Canceled. Do nothing
                        });
                detailsAlert.show();
            }
        }
    }

    public static void LaunchOverrideCultureDialog(final Activity activity, final BingMapsView bingMapsView) {
        final View cultureView = activity.getLayoutInflater().inflate(R.layout.culture_input, (ViewGroup) activity.findViewById(R.id.cultureInputView));

        AlertDialog.Builder cultureAlert = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.culture_mkt_param))
                .setIcon(android.R.drawable.ic_menu_search)
                .setView(cultureView)
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, whichButton) -> {
                    // Canceled. Do nothing
                })
                .setPositiveButton(activity.getString(R.string.change), (dialog, whichButton) -> {
                    EditText input = (EditText) cultureView.findViewById(R.id.cultureInput);
                    bingMapsView.overrideCulture(input.getText().toString().trim());
                });
        cultureAlert.show();
    }


}
