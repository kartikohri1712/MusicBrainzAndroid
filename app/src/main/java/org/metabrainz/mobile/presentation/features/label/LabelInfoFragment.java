package org.metabrainz.mobile.presentation.features.label;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.metabrainz.mobile.data.sources.api.entities.mbentity.Label;
<<<<<<< HEAD
import org.metabrainz.mobile.databinding.FragmentLabelInfoBinding;
=======
>>>>>>> b793e03... Improve usage of live data and reactive patterns.

public class LabelInfoFragment extends Fragment {

    private FragmentLabelInfoBinding binding;
    private LabelViewModel labelViewModel;

    public static LabelInfoFragment newInstance() {
        return new LabelInfoFragment();
    }

    @Override
<<<<<<< HEAD
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLabelInfoBinding.inflate(inflater, container, false);
        labelViewModel = new ViewModelProvider(requireActivity()).get(LabelViewModel.class);
        labelViewModel.getData().observe(getViewLifecycleOwner(), this::setLabelInfo);
        return binding.getRoot();
=======
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_label_info, container, false);
<<<<<<< HEAD
        labelViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(LabelViewModel.class);
        labelViewModel.initializeData().observe(getViewLifecycleOwner(), this::setLabelInfo);
=======
        labelViewModel = new ViewModelProvider(requireActivity()).get(LabelViewModel.class);
        labelViewModel.getData().observe(getViewLifecycleOwner(), this::setLabelInfo);
>>>>>>> b793e03... Improve usage of live data and reactive patterns.
        findViews(layout);
        return layout;
>>>>>>> de8f646... Refactor lookup repositories to remove redundancy.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setLabelInfo(Label label) {
        if (label != null) {
            String type, founded, area, code;

            type = label.getType();
            code = label.getCode();

            if (label.getLifeSpan() != null && label.getLifeSpan().getBegin() != null &&
                    !label.getLifeSpan().getBegin().isEmpty())
                founded = label.getLifeSpan().getBegin();
            else founded = "";
            if (label.getArea() != null) area = label.getArea().getName();
            else area = "";

            if (type != null && !type.isEmpty())
                binding.labelType.setText(type);
            if (founded != null && !founded.isEmpty())
                binding.labelFounded.setText(founded);
            if (area != null && !area.isEmpty())
                binding.labelArea.setText(area);
            if (code != null && !code.isEmpty())
                binding.labelCode.setText(code);
        }
    }

}
