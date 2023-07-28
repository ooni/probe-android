package org.openobservatory.ooniprobe.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.databinding.FragmentContentBinding;

public class PreferenceGlobalFragment extends Fragment {

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		FragmentContentBinding binding = FragmentContentBinding.inflate(inflater);
		((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
		return binding.getRoot();
	}

	@Override
	public void onStart() {
		super.onStart();
		getParentFragmentManager().beginTransaction().replace(R.id.subContent, PreferenceFragment.newInstance(R.xml.preferences_global, R.id.subContent, null)).commit();
	}
}
