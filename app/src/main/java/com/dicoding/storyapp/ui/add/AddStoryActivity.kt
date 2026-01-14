package com.dicoding.storyapp.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.ui.main.MainActivity
import com.dicoding.storyapp.utils.createImageTempFile
import com.dicoding.storyapp.utils.createImageUri
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels { ViewModelFactory.getInstance(this) }

    private var currentImageFile: File? = null
    private var cameraImageUri: Uri? = null
    private var currentLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    binding.switchLocation.isChecked = false
                }
            }
        }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val file = uriToFile(uri, this)
            currentImageFile = file
            showPreview(uri)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            cameraImageUri?.let { uri ->
                val file = uriToFile(uri, this)
                currentImageFile = file
                showPreview(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadStory() }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                currentLocation = null
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try again",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun startCamera() {
        val file = createImageTempFile(this)
        val uri = createImageUri(this, file)
        cameraImageUri = uri
        launcherCamera.launch(uri)
    }

    private fun showPreview(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(binding.ivPreview)
    }

    private fun uploadStory() {
        val file = currentImageFile
        val description = binding.edAddDescription.text?.toString().orEmpty().trim()

        if (file == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Deskripsi wajib diisi.", Toast.LENGTH_SHORT).show()
            return
        }

        val reducedFile = reduceFileImage(file)

        val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart = MultipartBody.Part.createFormData(
            "photo",
            reducedFile.name,
            requestImageFile
        )
        val descBody = description.toRequestBody("text/plain".toMediaType())

        var latBody: RequestBody? = null
        var lonBody: RequestBody? = null

        if (currentLocation != null) {
            latBody = currentLocation?.latitude.toString().toRequestBody("text/plain".toMediaType())
            lonBody = currentLocation?.longitude.toString().toRequestBody("text/plain".toMediaType())
        }

        viewModel.uploadStory(imageMultipart, descBody, latBody, lonBody).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data.message ?: "Upload berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonAdd.isEnabled = !isLoading
        binding.btnCamera.isEnabled = !isLoading
        binding.btnGallery.isEnabled = !isLoading
    }
}
