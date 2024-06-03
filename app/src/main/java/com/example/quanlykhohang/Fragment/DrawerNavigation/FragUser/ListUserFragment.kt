package com.example.quanlykhohang.Fragment.DrawerNavigation.FragUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quanlykhohang.Adapter.UserAdpater
import com.example.quanlykhohang.Model.User
import com.example.quanlykhohang.R
import com.example.quanlykhohang.databinding.FragmentListUserBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListUserFragment : Fragment() {
    private lateinit var binding: FragmentListUserBinding
    private lateinit var listUser: ArrayList<User>
    private lateinit var adapter: UserAdpater

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_user, container, false)
        binding = FragmentListUserBinding.bind(view)
        listUser = ArrayList()
        loadData()
        feachData()
        return view
    }

    private fun loadData() {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdpater(listUser, requireContext())
        binding.recyclerview.adapter = adapter
    }

    private fun feachData() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listUser.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        listUser.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}