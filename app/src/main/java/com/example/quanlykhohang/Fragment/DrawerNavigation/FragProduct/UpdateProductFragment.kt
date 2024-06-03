package com.example.quanlykhohang.Fragment.DrawerNavigation.FragProduct

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentUpdateProductBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst

class UpdateProductFragment : Fragment() {

    // Biến để giữ đối tượng ViewBinding
    private var _binding: FragmentUpdateProductBinding? = null
    private val binding get() = _binding!!

    // Tham chiếu đến Firebase Storage
    private lateinit var storageReference: StorageReference

    // Sản phẩm hiện tại và URI của hình ảnh được chọn
    private var currentProduct: Product? = null
    private var selectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho Fragment sử dụng ViewBinding
        _binding = FragmentUpdateProductBinding.inflate(inflater, container, false)
        val view = binding.root

        // Khởi tạo tham chiếu đến Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Nhận dữ liệu sản phẩm được truyền từ Fragment trước
        currentProduct = arguments?.getParcelable("product")

        // Hiển thị thông tin sản phẩm hiện tại lên giao diện
        currentProduct?.let { product ->
            binding.txtTenSP.setText(product.name) // Đặt tên sản phẩm
            binding.txtGiaSP.setText(product.price.toString()) // Đặt giá sản phẩm
            binding.txtSoLuong.setText(product.quantity.toString()) // Đặt số lượng sản phẩm
            if (!product.photo.isNullOrEmpty()) {
                // Load ảnh sản phẩm sử dụng Glide
                Glide.with(requireContext()).load(product.photo).into(binding.imgHinhSP)
            }
        }

        // Xử lý sự kiện khi nhấn vào hình ảnh để chọn ảnh mới
        binding.imgHinhSP.setOnClickListener {
            openImagePicker()
        }

        // Xử lý sự kiện khi nhấn vào nút cập nhật sản phẩm
        binding.btnSuaSP.setOnClickListener {
            updateProduct()
        }

        // Xử lý sự kiện khi nhấn vào nút hủy
        binding.btnHuySP.setOnClickListener {
            parentFragmentManager.popBackStack() // Quay lại Fragment trước
        }

        return view
    }

    // Phương thức để mở trình chọn ảnh
    private fun openImagePicker() {
        FilePickerBuilder.instance
            .setActivityTitle("Chọn ảnh")
            .setMaxCount(1)
            .pickPhoto(this)
    }

    // Phương thức để cập nhật sản phẩm
    private fun updateProduct() {
        // Lấy thông tin sản phẩm cập nhật từ các trường nhập liệu
        val updatedName = binding.txtTenSP.text.toString()
        val updatedPrice = binding.txtGiaSP.text.toString().toIntOrNull()
        val updatedQuantity = binding.txtSoLuong.text.toString().toIntOrNull()

        // Kiểm tra các trường nhập liệu
        if (updatedName.isNotEmpty() && updatedPrice != null && updatedQuantity != null) {
            currentProduct?.let { product ->
                product.name = updatedName
                product.price = updatedPrice
                product.quantity = updatedQuantity

                // Nếu có ảnh mới được chọn, xóa ảnh cũ và tải lên ảnh mới
                selectedUri?.let { newImageUri ->
                    deleteOldImageAndUploadNew(product, newImageUri)
                } ?: run {
                    // Nếu không có ảnh mới, cập nhật thông tin sản phẩm trong cơ sở dữ liệu
                    updateProductInDatabase(product)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    // Phương thức để xóa ảnh cũ và tải lên ảnh mới
    private fun deleteOldImageAndUploadNew(product: Product, newImageUri: Uri) {
        currentProduct?.photo?.let { oldImageUri ->
            // Xóa ảnh cũ từ Firebase Storage
            val oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUri)
            oldImageRef.delete().addOnSuccessListener {
                // Nếu xóa ảnh cũ thành công, tải lên ảnh mới và cập nhật sản phẩm
                uploadImageAndSaveProduct(product, newImageUri)
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Xóa ảnh cũ thất bại: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProductFragment", "Xóa ảnh cũ thất bại", error)
            }
        }
    }

    // Phương thức để tải lên ảnh mới và cập nhật sản phẩm
    private fun uploadImageAndSaveProduct(product: Product, imageUri: Uri) {
        val filePath = storageReference.child("product_images").child("${System.currentTimeMillis()}.jpg")
        filePath.putFile(imageUri)
            .addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    product.photo = uri.toString()
                    updateProductInDatabase(product)
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Tải ảnh lên thất bại: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProductFragment", "Tải ảnh lên thất bại", error)
            }
    }

    // Phương thức để cập nhật sản phẩm trong cơ sở dữ liệu
    private fun updateProductInDatabase(product: Product) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        myRef.child(product.id.toString()).setValue(product)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Quay lại Fragment trước
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Cập nhật sản phẩm thất bại", Toast.LENGTH_SHORT).show()
                Log.e("UpdateProductFragment", "Cập nhật sản phẩm thất bại", error)
            }
    }

    // Xử lý kết quả trả về từ trình chọn ảnh
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            val photos = data?.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
            if (!photos.isNullOrEmpty()) {
                selectedUri = photos[0]
                binding.imgHinhSP.setImageURI(selectedUri)
            }
        }
    }

    // Hủy tham chiếu đến view binding khi Fragment bị hủy
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
