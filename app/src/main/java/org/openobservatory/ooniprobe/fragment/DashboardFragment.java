package org.openobservatory.ooniprobe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.activity.AbstractActivity;
import org.openobservatory.ooniprobe.activity.MainActivity;
import org.openobservatory.ooniprobe.activity.OverviewActivity;
import org.openobservatory.ooniprobe.activity.RunningActivity;
import org.openobservatory.ooniprobe.common.Application;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ReachabilityManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.item.SeperatorItem;
import org.openobservatory.ooniprobe.item.TestsuiteItem;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.TestAsyncTask;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerAdapter;
import localhost.toolkit.widget.recyclerview.HeterogeneousRecyclerItem;

public class DashboardFragment extends Fragment implements View.OnClickListener {
	@BindView(R.id.recycler) RecyclerView recycler;
	@BindView(R.id.toolbar) Toolbar toolbar;
	@BindView(R.id.last_tested) TextView lastTested;
    @BindView(R.id.run_all) TextView runAll;
	@BindView(R.id.vpn) TextView vpn;

	@Inject
	PreferenceManager preferenceManager;

	private ArrayList<HeterogeneousRecyclerItem> items;
	private ArrayList<AbstractSuite> testSuites;
	private HeterogeneousRecyclerAdapter<HeterogeneousRecyclerItem> adapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
		ButterKnife.bind(this, v);
		((Application) getActivity().getApplication()).getFragmentComponent().inject(this);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
		items = new ArrayList<>();
		testSuites = new ArrayList<>();
		adapter = new HeterogeneousRecyclerAdapter<>(getActivity(), items);
		recycler.setAdapter(adapter);
		recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
		runAll.setOnClickListener(v1 -> runAll());
		vpn.setOnClickListener(view -> ((Application) getActivity().getApplication()).openVPNSettings());
		return v;
	}

	@Override public void onResume() {
		super.onResume();
		items.clear();
		testSuites.clear();
		testSuites.addAll(TestAsyncTask.getSuites(getResources()));

		ArrayList<AbstractSuite> emptySuites = new ArrayList<>();
		for (AbstractSuite testSuite : testSuites){
			if(testSuite.getTestList(preferenceManager).length > 0){
				items.add(new TestsuiteItem(testSuite, this, preferenceManager));
			} else {
				emptySuites.add(testSuite);
			}
		}

		if(!emptySuites.isEmpty()){
			items.add(new SeperatorItem());

			for(AbstractSuite emptyTest: emptySuites)
				items.add(new TestsuiteItem(emptyTest, this, preferenceManager));
		}



		setLastTest();
		adapter.notifyTypesChanged();
		if (ReachabilityManager.isVPNinUse(this.getContext())
				&& preferenceManager.isWarnVPNInUse())
			vpn.setVisibility(View.VISIBLE);
		else
			vpn.setVisibility(View.GONE);
	}

	private void setLastTest() {
		Result lastResult = Result.getLastResult();
		if (lastResult == null)
			lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
					+ " " +
					getString(R.string.Dashboard_Overview_LastRun_Never));
		else
			lastTested.setText(getString(R.string.Dashboard_Overview_LatestTest)
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
