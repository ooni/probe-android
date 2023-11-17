package org.openobservatory.ooniprobe.fragment;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.openobservatory.ooniprobe.BuildConfig;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.databinding.FragmentDashboardBinding;
import org.openobservatory.ooniprobe.item.SeperatorItem;
import org.openobservatory.ooniprobe.item.TestsuiteItem;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;

import javax.inject.Inject;

import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class DashboardFragment extends Fragment implements View.OnClickListener {

	@Inject
	PreferenceManager preferenceManager;

	private ArrayList<HeterogeneousRecyclerItem> items;

	private ArrayList<AbstractSuite> testSuites;

	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;

	private FragmentDashboardBinding binding;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		binding = FragmentDashboardBinding.inflate(inflater,container,false);
		((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
		((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
		items = new ArrayList<>();
		testSuites = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		binding.recycler.setAdapter(adapter);
		binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		binding.runAll.setOnClickListener(v1 -> runAll());
		binding.vpn.setOnClickListener(view -> ((Application) getActivity().getApplication()).openVPNSettings());
		return binding.getRoot();
	}

	@Override public void onResume() {
		super.onResume();
		items.clear();
		testSuites.clear();
		testSuites.addAll(TestAsyncTask.getSuites());

		ArrayList<AbstractSuite> emptySuites = new ArrayList<>();
		for (AbstractSuite testSuite : testSuites){
			if(testSuite.getTestList(preferenceManager).length > 0){
				items.add(new TestsuiteItem(testSuite, this, preferenceManager));
			} else {
				emptySuites.add(testSuite);
			}
		}

		if(BuildConfig.SHOW_DISABLED_CARDS && !emptySuites.isEmpty()){
			items.add(new SeperatorItem());

			for(AbstractSuite emptyTest: emptySuites)
				items.add(new TestsuiteItem(emptyTest, this, preferenceManager));
		}



		setLastTest();
		adapter.notifyTypesChanged();
		if (ReachabilityManager.isVPNinUse(this.getContext())
				&& preferenceManager.isWarnVPNInUse())
			binding.vpn.setVisibility(View.VISIBLE);
		else
			binding.vpn.setVisibility(View.GONE);
	}

	private void setLastTest() {
		Result lastResult = Result.getLastResult();
		if (lastResult == null)
			binding.lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
					+ " " +
					getString(R.string.Dashboard_Overview_LastRun_Never));
		else
			binding.lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
					+ " " +
					DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()));
	}

    public void runAll() {
        RunningActivity.runAsForegroundService((AbstractActivity) getActivity(), testSuites, this::onTestServiceStartedListener, preferenceManager);
    }

    private void onTestServiceStartedListener() {
        try {
            ((AbstractActivity) getActivity()).bindTestService();
        } catch (Exception e) {
            e.printStackTrace();
            ThirdPartyServices.logException(e);
        }
    }

	@Override public void onClick(View v) {
		AbstractSuite testSuite = (AbstractSuite) v.getTag();
		switch (v.getId()) {
			case R.id.run:
                RunningActivity.runAsForegroundService(
                        (AbstractActivity) getActivity(),
                        testSuite.asArray(),
                        this::onTestServiceStartedListener,
						preferenceManager
                );
				break;
			default:
				ActivityCompat.startActivity(getActivity(), OverviewActivity.newIntent(getActivity(), testSuite), null);
				break;
		}
	}
}
