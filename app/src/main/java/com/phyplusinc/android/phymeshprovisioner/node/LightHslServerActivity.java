package com.phyplusinc.android.phymeshprovisioner.node;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.phyplusinc.android.phymeshprovisioner.R;
import com.phyplusinc.android.phymeshprovisioner.node.dialog.BottomSheetLightHSLDialogFragment;

import javax.inject.Inject;

import no.nordicsemi.android.meshprovisioner.ApplicationKey;
import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.models.LightHslServer;
import no.nordicsemi.android.meshprovisioner.transport.Element;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffGet;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffSet;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffStatus;
import no.nordicsemi.android.meshprovisioner.transport.LightHslGet;
import no.nordicsemi.android.meshprovisioner.transport.LightHslSet;
import no.nordicsemi.android.meshprovisioner.transport.LightHslSetUnacknowledged;
import no.nordicsemi.android.meshprovisioner.transport.LightHslStatus;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import no.nordicsemi.android.meshprovisioner.transport.MeshModel;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.utils.MeshAddress;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

public class LightHslServerActivity extends BaseModelConfigurationActivity {

    private static final String TAG = LightHslServerActivity.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private TextView    mTvHueValu;
    private SeekBar     mSbHueSeek;
    private TextView    mTvSatValu;
    private SeekBar     mSbSatSeek;
    private TextView    mTvLitValu;
    private SeekBar     mSbLitSeek;
    private Button      mBtHslRed;
    private Button      mBtHslGrn;
    private Button      mBtHslBlu;

    private Button      mBtHslGet;

    private final View.OnClickListener mRGBOnClickListener = view -> {
        float outHsl[] = new float[3];
        if (R.id.button_red == view.getId()) {
            ColorUtils.colorToHSL(Color.rgb(0xff, 0x00, 0x00), outHsl);
        } else
        if (R.id.button_green == view.getId()) {
            ColorUtils.colorToHSL(Color.rgb(0x00, 0xff, 0x00), outHsl);
        } else
        if (R.id.button_blue == view.getId()) {
            ColorUtils.colorToHSL(Color.rgb(0x00, 0x00, 0xff), outHsl);
        }

        mSbHueSeek.setProgress((int)outHsl[0]);
        mSbSatSeek.setProgress((int)(outHsl[1]*100F));
        mSbLitSeek.setProgress((int)(outHsl[2]*100F));

        final int hueValue = (int) (((float)0xFFFF * (float)mSbHueSeek.getProgress()) / 360F);
        final int satValue = (int) (((float)0xFFFF * (float)mSbSatSeek.getProgress()) / 100F);
        final int ligValue = (int) (((float)0xFFFF * (float)mSbLitSeek.getProgress()) / 100F);
        try {
            SendHslSet(hueValue, satValue, ligValue);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final SeekBar.OnSeekBarChangeListener mHSLOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (R.id.hue_seekbar == seekBar.getId()) {
                mTvHueValu.setText(getString(R.string.light_hue_interval, String.valueOf(progress)));
            } else
            if (R.id.sat_seekbar == seekBar.getId()) {
                mTvSatValu.setText(getString(R.string.light_saturation_interval, String.valueOf(progress)));
            } else
            if (R.id.lig_seekbar == seekBar.getId()) {
                mTvLitValu.setText(getString(R.string.light_lightness_interval, String.valueOf(progress)));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            final int hueValue = (int) (((float)0xFFFF * (float)mSbHueSeek.getProgress()) / 360F);
            final int satValue = (int) (((float)0xFFFF * (float)mSbSatSeek.getProgress()) / 100F);
            final int ligValue = (int) (((float)0xFFFF * (float)mSbLitSeek.getProgress()) / 100F);

            try {
                SendHslSet(hueValue, satValue, ligValue);
            } catch (IllegalArgumentException ex) {
                Toast.makeText(getParent(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MeshModel model = mViewModel.getSelectedModel().getValue();
        if (model instanceof LightHslServer) {
            final ConstraintLayout container = findViewById(R.id.node_controls_container);
            final View nodeControlsContainer = LayoutInflater.from(this).inflate(R.layout.layout_light_hsl, container);

            mTvHueValu = nodeControlsContainer.findViewById(R.id.hue_levl);
            mSbHueSeek = nodeControlsContainer.findViewById(R.id.hue_seekbar);
            mSbHueSeek.setProgress(0);
            mSbHueSeek.setMax(360);

            mTvSatValu = nodeControlsContainer.findViewById(R.id.saturation_levl);
            mSbSatSeek = nodeControlsContainer.findViewById(R.id.sat_seekbar);
            mSbSatSeek.setProgress(100);
            mSbSatSeek.setMax(100);

            mTvLitValu = nodeControlsContainer.findViewById(R.id.lightness_levl);
            mSbLitSeek = nodeControlsContainer.findViewById(R.id.lig_seekbar);
            mSbLitSeek.setProgress(50);
            mSbLitSeek.setMax(100);

            mSbHueSeek.setOnSeekBarChangeListener(mHSLOnSeekBarChangeListener);
            mSbSatSeek.setOnSeekBarChangeListener(mHSLOnSeekBarChangeListener);
            mSbLitSeek.setOnSeekBarChangeListener(mHSLOnSeekBarChangeListener);

            mBtHslRed = nodeControlsContainer.findViewById(R.id.button_red);
            mBtHslGrn = nodeControlsContainer.findViewById(R.id.button_green);
            mBtHslBlu = nodeControlsContainer.findViewById(R.id.button_blue);

            mBtHslRed.setOnClickListener(mRGBOnClickListener);
            mBtHslGrn.setOnClickListener(mRGBOnClickListener);
            mBtHslBlu.setOnClickListener(mRGBOnClickListener);

            mBtHslGet = nodeControlsContainer.findViewById(R.id.action_read);
            mBtHslGet.setOnClickListener(view -> SendHslGet());
        }
    }

    @Override
    protected void enableClickableViews() {
        super.enableClickableViews();
//        if (mActionOnOff != null && !mActionOnOff.isEnabled())
//            mActionOnOff.setEnabled(true);
    }

    @Override
    protected void disableClickableViews() {
        super.disableClickableViews();
//        if (mActionOnOff != null)
//            mActionOnOff.setEnabled(false);
    }

    @Override
    protected void updateMeshMessage(final MeshMessage meshMessage) {
        super.updateMeshMessage(meshMessage);
        if (meshMessage instanceof LightHslStatus) {
            hideProgressBar();
            final LightHslStatus status = (LightHslStatus) meshMessage;
            mSbHueSeek.setProgress(status.getPresentHue()*360/0xFFFF);
            mSbSatSeek.setProgress(status.getPresentSaturation()*100/0xFFFF);
            mSbLitSeek.setProgress(status.getPresentLightness()*100/0xFFFF);
        }
    }

    public void SendHslGet() {
        final Element element = mViewModel.getSelectedElement().getValue();
        if (element != null) {
            final MeshModel model = mViewModel.getSelectedModel().getValue();
            if (model != null) {
                if (!model.getBoundAppKeyIndexes().isEmpty()) {
                    final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                    final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);

                    final int address = element.getElementAddress();
                    Log.v(TAG, "Sending message to element's unicast address: " + MeshAddress.formatAddress(address, true));

                    final LightHslGet lightHslGet = new LightHslGet(appKey);
                    sendMessage(address, lightHslGet);
                } else {
                    mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                }
            }
        }
    }

    public void SendHslSet(int hue, int sat, int lit) {
        final ProvisionedMeshNode node = mViewModel.getSelectedMeshNode().getValue();
        if (node != null) {
            final Element element = mViewModel.getSelectedElement().getValue();
            if (element != null) {
                final MeshModel model = mViewModel.getSelectedModel().getValue();
                if (model != null) {
                    if (!model.getBoundAppKeyIndexes().isEmpty()) {
                        final int appKeyIndex = model.getBoundAppKeyIndexes().get(0);
                        final ApplicationKey appKey = mViewModel.getNetworkLiveData().getMeshNetwork().getAppKey(appKeyIndex);
                        final int tid = mViewModel.getNetworkLiveData().getMeshNetwork().getSelectedProvisioner().getSequenceNumber();
                        final int address = element.getElementAddress();
                        final LightHslSet lightHslSet = new LightHslSet(appKey, lit, hue, sat, tid);
                        sendMessage(address, lightHslSet);
                    } else {
                        mViewModel.displaySnackBar(this, mContainer, getString(R.string.error_no_app_keys_bound), Snackbar.LENGTH_LONG);
                    }
                }
            }
        }
    }
}
