package com.example.quanlykhohang.Adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlykhohang.Fragment.DrawerNavigation.FragProduct.UpdateProductFragment
import com.example.quanlykhohang.Model.Product
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.ItemProductBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

// Adapter để hiển thị danh sách sản phẩm
class ProductAdapter(private var listProduct: ArrayList<Product>, private var context: Context) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private lateinit var binding: ItemProductBinding

    // Lớp ViewHolder để tái sử dụng view
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // Tạo ViewHolder mới
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    // Số lượng sản phẩm trong danh sách
    override fun getItemCount(): Int {
        return listProduct.size
    }

    // Gắn dữ liệu vào ViewHolder tại vị trí đã chỉ định
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        binding = ItemProductBinding.bind(holder.itemView)
        val product = listProduct[position]

        // Hiển thị thông tin sản phẩm
        binding.txtMaSP.text = "Mã sản phẩm: ${product.id}"
        binding.txtTenSP.text = "Tên sản phẩm: ${product.name}"
        binding.txtGiaSP.text = "Giá sản phẩm: ${product.price}"
        binding.txtSoLuong.text = "Số lượng: ${product.quantity}"
        binding.txtNguoiThem.text = "Người thêm: ${product.userID}"

        // Load và hiển thị hình ảnh sản phẩm từ đường dẫn
        val photoPath = product.photo
        if (photoPath != null) {
            Glide.with(binding.imgHinhSP.context)
                .load(Uri.parse(photoPath))
                .into(binding.imgHinhSP)
        } else {
            // Nếu không có hình ảnh, sử dụng hình ảnh mặc định
            binding.imgHinhSP.setImageResource(R.drawable.img)
        }

        // Xử lý sự kiện khi người dùng nhấn nút "Update"
        binding.btnUpdateSP.setOnClickListener {
            // Tạo Bundle chứa thông tin sản phẩm cần cập nhật
            val bundle = Bundle()
            bundle.putParcelable("product", product)

            // Tạo Fragment UpdateProductFragment và truyền Bundle vào
            val fragment = UpdateProductFragment()
            fragment.arguments = bundle

            // Thay thế Fragment hiện tại bằng UpdateProductFragment
            val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fame, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Xử lý sự kiện khi người dùng nhấn nút "Delete"
        binding.btnDeleteSP.setOnClickListener {
            showDeleteConfirmationDialog(position)
        }

    }
    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Xác nhận xóa")
        alertDialogBuilder.setMessage("Bạn có chắc muốn xóa sản phẩm này?")
        alertDialogBuilder.setPositiveButton("Xóa") { dialog, _ ->
            val productId = listProduct[position].id
            val photoPath = listProduct[position].photo
            // Xóa sản phẩm từ Firebase
            deleteProductFromFirebase(productId, photoPath)
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun deleteProductFromFirebase(productId: Int, photoPath: String?) {
        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("Products")

        // Truy cập vào nút sản phẩm cần xóa và gọi phương thức removeValue()
        productsRef.child(productId.toString()).removeValue()
            .addOnSuccessListener {
                // Xóa sản phẩm thành công từ Firebase
                Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                // Nếu có đường dẫn ảnh, thực hiện xóa ảnh từ Firebase Storage
                photoPath?.let { deletePhotoFromFirebaseStorage(it) }
            }
            .addOnFailureListener { exception ->
                // Xóa sản phẩm thất bại, xử lý lỗi nếu cần
                Log.e(TAG, "Error deleting product from Firebase: $exception")
                // Hiển thị thông báo lỗi (nếu cần)
                Toast.makeText(context, "Lỗi: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deletePhotoFromFirebaseStorage(photoPath: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoPath)

        // Xóa ảnh từ Firebase Storage
        storageRef.delete()
            .addOnSuccessListener {
                // Xóa ảnh thành công
                Log.d(TAG, "Photo deleted from Firebase Storage")
            }
            .addOnFailureListener { exception ->
                // Xóa ảnh thất bại, xử lý lỗi nếu cần
                Log.e(TAG, "Error deleting photo from Firebase Storage: $exception")
            }
    }

    // Phương thức để cập nhật dữ liệu của Adapter
    //fun setData(newData: ArrayList<Product>) {
    //    if (newData.isEmpty()) return
    //    listProduct = newData
    //    notifyDataSetChanged()
    //}
}
