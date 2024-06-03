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
import com.example.quanlykhohang.Interface.Notification
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentAddProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

// Định nghĩa Fragment và triển khai các interface cần thiết cho quyền và thông báo
class AddProductFragment : Fragment(), EasyPermissions.PermissionCallbacks, Notification {

    // Khai báo các biến cho view binding, URI của hình ảnh, danh sách sản phẩm và tham chiếu lưu trữ
    private lateinit var binding: FragmentAddProductBinding
    private var listUri = arrayListOf<Uri>()
    private var productList = ArrayList<Product>()
    private lateinit var storageReference: StorageReference

    // Tạo view cho Fragment và khởi tạo các thành phần
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        binding = FragmentAddProductBinding.bind(view)

        // Khởi tạo tham chiếu Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        // Thiết lập sự kiện click cho nút hủy
        binding.btnHuySP.setOnClickListener {
            parentFragmentManager.popBackStack()  // Quay lại Fragment trước đó
        }

        // Thiết lập sự kiện click cho nút chọn hình ảnh
        binding.imgHinhSP.setOnClickListener {
            grantPermissions()  // Yêu cầu quyền truy cập ảnh
        }

        // Lấy danh sách sản phẩm từ cơ sở dữ liệu
        getProduct()

        // Thiết lập sự kiện click cho nút thêm sản phẩm
        binding.btnThemSP.setOnClickListener {
            getUserID()  // Lấy ID người dùng hiện tại và thêm sản phẩm
        }
        return view
    }

    // Yêu cầu các quyền cần thiết để truy cập bộ nhớ ngoài và camera
    private fun grantPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        // Kiểm tra nếu quyền đã được cấp
        if (EasyPermissions.hasPermissions(requireContext(), *permissions)) {
            imagePicker()  // Mở trình chọn hình ảnh nếu quyền đã được cấp
        } else {
            // Yêu cầu quyền
            EasyPermissions.requestPermissions(
                this,
                "Cấp quyền truy cập ảnh",
                100,
                *permissions
            )
        }
    }

    // Mở trình chọn hình ảnh để chọn hình ảnh
    private fun imagePicker() {
        FilePickerBuilder.instance
            .setActivityTitle("Chọn ảnh")  // Đặt tiêu đề cho trình chọn hình ảnh
            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)  // Đặt khoảng cách cho chế độ xem thư mục
            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)  // Đặt khoảng cách cho chế độ xem chi tiết
            .setMaxCount(1)  // Đặt số lượng hình ảnh tối đa có thể chọn
            .setSelectedFiles(listUri)  // Truyền URI đã chọn
            .setActivityTheme(R.style.CustomTheme)  // Đặt chủ đề cho trình chọn hình ảnh
            .pickPhoto(this)  // Bắt đầu trình chọn hình ảnh
    }

    // Lấy danh sách sản phẩm từ cơ sở dữ liệu Firebase
    private fun getProduct() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (productList != null) {
                    productList.clear()  // Xóa danh sách sản phẩm hiện tại
                }
                for (snapshot: DataSnapshot in snapshot.children) {
                    val product = snapshot.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)  // Thêm sản phẩm vào danh sách
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    // Lấy ID người dùng hiện tại và tải lên hình ảnh
    private fun getUserID() {
        val users: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (users == null) {
            return  // Trả về nếu không có người dùng đăng nhập
        }
        val email = users.email
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user?.email.equals(email)) {
                        if (user != null) {
                            uploadImage(user.id.toString())  // Tải lên hình ảnh với ID người dùng
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

    // Tải lên hình ảnh đến Firebase Storage
    private fun uploadImage(userID: String) {
        if (listUri.isNotEmpty()) {
            val filePath = storageReference.child("product_images").child("${System.currentTimeMillis()}.jpg")
            filePath.putFile(listUri[0])
                .addOnSuccessListener { taskSnapshot ->
                    filePath.downloadUrl.addOnSuccessListener { uri ->
                        addProduct(userID, uri.toString())  // Thêm sản phẩm với URL hình ảnh
                    }
                }
                .addOnFailureListener { error ->
                    notificationError("Upload image failed: ${error.message}")
                    Log.e("AddProductFragment", "Failed to upload image", error)
                }
        } else {
            notificationError("No image selected")
        }
    }

    // Thêm sản phẩm vào cơ sở dữ liệu
    private fun addProduct(userID: String, imageUrl: String) {
        val iduser = userID
        var size = 0
        var id = 0
        if (productList.isEmpty()) {
            id = 1
        } else {
            size = productList.size - 1
            id = productList[size].id!! + 1
        }
        val newProduct = Product(
            id,
            binding.txtTenSP.text.toString(),
            binding.txtSoLuong.text.toString().toInt(),
            binding.txtGiaSP.text.toString().toInt(),
            imageUrl,
            "0",
            iduser
        )
        addData(newProduct)  // Thêm dữ liệu sản phẩm vào cơ sở dữ liệu
    }

    // Thêm dữ liệu sản phẩm vào Firebase Database
    private fun addData(product: Product) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Products")
        val productID = product.id.toString()

        myRef.child(productID).setValue(product)
            .addOnSuccessListener {
                notificationSuccess("Thêm sản phẩm thành công!")
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { error ->
                notificationError("Thêm sản phẩm thất bại!")
                Log.e("AddProductFragment", "Failed to add product", error)
            }
    }

    // Callbacks cho quyền và kết quả hoạt động
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
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

    // Hiển thị thông báo thành công
    override fun notificationSuccess(messageSuccess: String) {
        Toast.makeText(requireContext(), messageSuccess, Toast.LENGTH_SHORT).show()
    }

    // Hiển thị thông báo lỗi
    override fun notificationError(messageError: String) {
        Toast.makeText(requireContext(), messageError, Toast.LENGTH_SHORT).show()
    }
}
