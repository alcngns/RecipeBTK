package com.alicangunes.recipebtk.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.alicangunes.recipebtk.databinding.FragmentDepictionBinding
import com.alicangunes.recipebtk.model.Depiction
import com.alicangunes.recipebtk.roomdb.DepictionDAO
import com.alicangunes.recipebtk.roomdb.DepictionDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import kotlin.math.max

class DepictionFragment : Fragment() {

    private var _binding: FragmentDepictionBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImage : Uri? = null
    private var selectedBitmap : Bitmap? = null
    private lateinit var db : DepictionDatabase
    private lateinit var depictionDao : DepictionDAO
    private val mDisposable = CompositeDisposable()
    private var selectedDepiction : Depiction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext(), DepictionDatabase::class.java, "Depictions").build()
        depictionDao = db.DepictionDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDepictionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener { selectImage(it) }
        binding.saveButton.setOnClickListener { save(it) }
        binding.deleteButton.setOnClickListener { delete(it) }

        arguments?.let {
            val info = DepictionFragmentArgs.fromBundle(it).information

            if (info == "new") {
                binding.deleteButton.isEnabled = false
                binding.saveButton.isEnabled = true
                binding.nameText.setText("")
                binding.materialText.setText("")
            } else {
                binding.deleteButton.isEnabled = true
                binding.saveButton.isEnabled = false
                val id = DepictionFragmentArgs.fromBundle(it).id


                mDisposable.add(
                    depictionDao.findById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse)
                )
            }
        }

    }

    private fun handleResponse (depiction: Depiction) {
        val bitmap = BitmapFactory.decodeByteArray(depiction.image, 0, depiction.image.size)
        binding.imageView.setImageBitmap(bitmap)
        binding.nameText.setText(depiction.name)
        binding.materialText.setText(depiction.material)
        selectedDepiction = depiction

    }

    fun save(view: View) {
        val name = binding.nameText.text.toString()
        val material = binding.materialText.toString()

        if (selectedBitmap != null) {
            val smallBitmap = smallBitmap(selectedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val byteArray = outputStream.toByteArray()

            val depiction = Depiction(name, material, byteArray)

            //RxJava
            mDisposable.add(   depictionDao.insert(depiction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert))

        }


    }

    private fun handleResponseForInsert() {
        // go to former action
        val action = DepictionFragmentDirections.actionDepictionFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)

    }

    fun delete(view: View) {
       if (selectedDepiction != null) {
           mDisposable.add(
               depictionDao.delete(depiction = selectedDepiction!!)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(this::handleResponseForInsert)
           )
    }

    }

    fun selectImage(view: View) {

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                //not permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    // show snackbar
                    Snackbar.make(view, "You need to give permission to access the gallery.", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Give Permission",
                        View.OnClickListener {
                            // ask permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    ).show()
                } else {
                    // ask permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }

        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //not permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // show snackbar
                    Snackbar.make(view, "You need to give permission to access the gallery.", Snackbar.LENGTH_INDEFINITE).setAction(
                        "Give Permission",
                        View.OnClickListener {
                            // ask permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    ).show()
                } else {
                    // ask permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                // permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }

    private fun registerLauncher() {


        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedImage = intentFromResult.data

                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source = ImageDecoder.createSource(requireActivity().contentResolver, selectedImage!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } else {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                    }
                }
            }

        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                //permission granted
                //go to gallery
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //no permission
                Toast.makeText(requireContext(), "You didn't let for go to gallery", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun smallBitmap(selectedBitmap : Bitmap, maxSize : Int) : Bitmap {
        var width = selectedBitmap.width
        var height = selectedBitmap.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio >= 1) {
            // image horizontal
            width = maxSize
            val smallHeight = width / bitmapRatio
            height = smallHeight.toInt()
        } else {
            //image vertical
            height = maxSize
            val smallWidth = height * bitmapRatio
            width = smallWidth.toInt()

        }

        return Bitmap.createScaledBitmap(selectedBitmap, width, height, true )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }


}