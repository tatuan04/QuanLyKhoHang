package com.example.quanlykhohang.Fragment.DrawerNavigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.quanlykhohang.Interface.TransferFragment
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddProductBinding
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class AddProductFragment : Fragment(), EasyPermissions.PermissionCallbacks {

private lateinit var binding: FragmentAddProductBinding
    private var listUri = arrayListOf<Uri>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        binding = FragmentAddProductBinding.bind(view)

        binding.btnHuySP.setOnClickListener {
            (requireActivity()as TransferFragment).transferFragment(ProductFragment(), "ProductFragment")
        }

        binding.imgHinhSP.setOnClickListener {
            grantPermissions()
        }

        binding.btnThemSP.setOnClickListener {
            addProduct()
        }
        return view
    }


    private fun grantPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        if (EasyPermissions.hasPermissions(requireContext(), *permissions)) {
            imagePicker()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Cấp quyền truy cập ảnh",
                100,
                *permissions
            )
        }
    }


    private fun imagePicker() {
        FilePickerBuilder.instance
            .setActivityTitle("Chọn ảnh")
            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
            .setMaxCount(1)
            .setSelectedFiles(listUri)
            .setActivityTheme(R.style.CustomTheme)
            .pickPhoto(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            listUri = data?.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)!!
            binding.imgHinhSP.setImageURI(listUri[0])
        }
    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == 100 && perms.size == 1) {
            imagePicker()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addProduct() {
        TODO("Not yet implemented")
    }

}