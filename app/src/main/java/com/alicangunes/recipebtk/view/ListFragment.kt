package com.alicangunes.recipebtk.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.alicangunes.recipebtk.adapter.DepictionAdapter
import com.alicangunes.recipebtk.databinding.FragmentListBinding
import com.alicangunes.recipebtk.model.Depiction
import com.alicangunes.recipebtk.roomdb.DepictionDAO
import com.alicangunes.recipebtk.roomdb.DepictionDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var db : DepictionDatabase
    private lateinit var depictionDao : DepictionDAO
    private val mDisposible = CompositeDisposable()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), DepictionDatabase::class.java, "Depictions").build()
        depictionDao = db.DepictionDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { addNew(it) }
        binding.depictionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        getData()
    }

    private fun getData() {
        mDisposible.add(
            depictionDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(depictions : List<Depiction>) {
        val adapter = DepictionAdapter(depictions)
        binding.depictionRecyclerView.adapter = adapter

    }

    fun addNew(view: View) {
        val action = ListFragmentDirections.actionListFragmentToDepictionFragment("new", -1)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposible.clear()
    }


}