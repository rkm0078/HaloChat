package com.rishabh.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rishabh.chatapp.R;
import com.rishabh.chatapp.adapters.CallAdapter;
import com.rishabh.chatapp.database.entity.CallEntity;
import com.rishabh.chatapp.database.repository.CallRepository;
import com.rishabh.chatapp.models.Call;

import java.util.ArrayList;

public class CallsFragment extends Fragment {

    private RecyclerView recyclerCalls;
    private View emptyLayout;
    private Button btnStartCall;

    private CallAdapter adapter;

    private final ArrayList<Call> callList =
            new ArrayList<>();

    private CallRepository callRepository;

    public CallsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_calls,
                container,
                false
        );

    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerCalls =
                view.findViewById(R.id.recyclerCalls);

        emptyLayout =
                view.findViewById(R.id.emptyLayout);

        btnStartCall =
                view.findViewById(R.id.btnStartCall);

        recyclerCalls.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        adapter =
                new CallAdapter(
                        requireContext(),
                        callList
                );

        recyclerCalls.setAdapter(adapter);

        updateEmptyState();


        callRepository =
                new CallRepository(requireActivity().getApplication());
        callRepository.getCalls().observe(
                getViewLifecycleOwner(),
                callEntities -> {

                    callList.clear();

                    for (CallEntity entity : callEntities) {

                        callList.add(mapToCall(entity));
                    }

                    callList.sort(
                            (c1, c2) ->
                                    Long.compare(
                                            c2.timestamp,
                                            c1.timestamp
                                    )
                    );

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    updateEmptyState();
                }
        );

        btnStartCall.setOnClickListener(v -> {

            // TODO
            // Open friend selector to start a call

            Toast.makeText(
                    requireContext(),
                    "Call feature will be available soon",
                    Toast.LENGTH_SHORT
            ).show();

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        recyclerCalls = null;
        emptyLayout = null;
        btnStartCall = null;
        adapter = null;
        callRepository = null;

        callList.clear();
    }

    private void updateEmptyState() {

        if (emptyLayout == null ||
                recyclerCalls == null) {
            return;
        }

        if (callList.isEmpty()) {

            emptyLayout.setVisibility(View.VISIBLE);
            recyclerCalls.setVisibility(View.GONE);

        } else {

            emptyLayout.setVisibility(View.GONE);
            recyclerCalls.setVisibility(View.VISIBLE);
        }
    }

    private Call mapToCall(CallEntity entity) {

        Call call = new Call();

        call.callId = entity.callId;
        call.userId = entity.userId;

        call.userName =
                entity.userName == null ? "" : entity.userName;

        call.profileImage =
                entity.profileImage == null ? "" : entity.profileImage;

        call.timestamp = entity.timestamp;
        call.isVideo = entity.isVideo;
        call.isIncoming = entity.isIncoming;
        call.isMissed = entity.isMissed;

        return call;
    }
}