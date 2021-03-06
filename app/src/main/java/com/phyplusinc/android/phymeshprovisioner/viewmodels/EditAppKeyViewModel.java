package com.phyplusinc.android.phymeshprovisioner.viewmodels;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import com.phyplusinc.android.phymeshprovisioner.keys.AppKeysActivity;

/**
 * ViewModel for {@link AppKeysActivity}
 */
public class EditAppKeyViewModel extends KeysViewModel {

    @Inject
    EditAppKeyViewModel(@NonNull final NrfMeshRepository nrfMeshRepository) {
        super(nrfMeshRepository);
    }
}
