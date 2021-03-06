package com.phyplusinc.android.phymeshprovisioner.viewmodels;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import no.nordicsemi.android.meshprovisioner.Provisioner;
import com.phyplusinc.android.phymeshprovisioner.keys.AppKeysActivity;

/**
 * ViewModel for {@link AppKeysActivity}
 */
public class EditProvisionerViewModel extends BaseViewModel {

    @Inject
    EditProvisionerViewModel(@NonNull final NrfMeshRepository nrfMeshRepository) {
        super(nrfMeshRepository);
    }

    public void setSelectedProvisioner(@NonNull final Provisioner provisioner) {
        mNrfMeshRepository.setSelectedProvisioner(provisioner);
    }

    public LiveData<Provisioner> getSelectedProvisioner() {
        return mNrfMeshRepository.getSelectedProvisioner();
    }
}
