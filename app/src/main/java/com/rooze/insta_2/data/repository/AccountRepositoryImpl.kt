package com.rooze.insta_2.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.rooze.insta_2.data.remote.DataConstants
import com.rooze.insta_2.domain.common.DataResult
import com.rooze.insta_2.domain.entity.Account
import com.rooze.insta_2.domain.repository.AccountRepository
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "AccountRepositoryImpl"

class AccountRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountRepository {
    override suspend fun getCurrentAccount(): DataResult<Account> {
        val firebaseUser = auth.currentUser ?: return DataResult.Fail("Login required")
        return firestore.getAccountInfo(firebaseUser.uid)
    }

    override suspend fun getCurrentAccountId(): DataResult<String> {
        val firebaseUser = auth.currentUser ?: return DataResult.Fail("Login required")
        return DataResult.Success(firebaseUser.uid)
    }

    override suspend fun getAccountById(accountId: String): DataResult<Account> {
        return firestore.getAccountInfo(accountId)
    }

    override suspend fun login(email: String, password: String): DataResult<Account> {
        val accountId: String = suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener { e ->
                    Log.e(TAG, "login: ", e)
                    continuation.resume(null)
                }.addOnSuccessListener { authResult ->
                    continuation.resume(authResult.user?.uid)
                }
        } ?: return DataResult.Fail("Failed to login")

        return firestore.getAccountInfo(accountId)
    }

    override suspend fun register(account: Account, password: String): DataResult<Account> {
        val accountId: String = suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(account.email, password)
                .addOnSuccessListener { authResult ->
                    continuation.resume(authResult.user?.uid)
                }.addOnFailureListener { e ->
                    Log.e(TAG, "register: ", e)
                    continuation.resume(null)
                }
        } ?: return DataResult.Fail("Failed to register")

        val nameUpdated = firestore.updateAccountName(accountId, account.name)

        return if (nameUpdated) {
            DataResult.Success(account.copy(id = accountId))
        } else {
            DataResult.Fail("Registered but fail to set name")
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun updateAccountInfo(avatarUrl: String?, name: String?): DataResult<Account> {
        val firebaseUser = auth.currentUser ?: return DataResult.Fail("Login required")
        val data = HashMap<String, Any>()
        avatarUrl?.let { data["avatarUrl"] = it }
        name?.let { data["name"] = it }

        Log.i(TAG, "updateAccountInfo: $data")

        return suspendCoroutine { continuation ->
            firestore.collection(DataConstants.ACCOUNT_COLLECTION)
                .document(firebaseUser.uid)
                .update(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        continuation.resume(DataResult.Success(Account(id = firebaseUser.uid)))
                    } else {
                        Log.e(TAG, "updateAccountInfo: ", it.exception)
                        continuation.resume(DataResult.Fail("Failed to update info"))
                    }
                }
        }
    }
}

fun DocumentSnapshot.toAccount(): Account {
    return Account(
        id = id,
        name = data?.get("name")?.toString() ?: "",
        email = data?.get("email")?.toString() ?: "",
        avatarUrl = data?.get("avatarUrl")?.toString() ?: ""
    )
}

private suspend fun FirebaseFirestore.getAccountInfo(
    accountId: String
): DataResult<Account> = suspendCoroutine { continuation ->
    collection(DataConstants.ACCOUNT_COLLECTION)
        .document(accountId)
        .get()
        .addOnSuccessListener { doc ->
            continuation.resume(DataResult.Success(doc.toAccount()))
        }.addOnFailureListener { e ->
            Log.e(TAG, "getAccountInfo: ", e)
            continuation.resume(DataResult.Fail("Can not load account info"))
        }
}

private suspend fun FirebaseFirestore.updateAccountName(
    accountId: String,
    name: String
): Boolean = suspendCoroutine { continuation ->
    collection(DataConstants.ACCOUNT_COLLECTION)
        .document(accountId)
        .set(hashMapOf("name" to name))
        .addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.e(TAG, "addAccount: ", it.exception)
            }
            continuation.resume(it.isSuccessful)
        }
}

private suspend fun FirebaseFirestore.updateAccountAvatar(
    accountId: String,
    avatarUrl: String
): Boolean = suspendCoroutine { continuation ->
    collection(DataConstants.ACCOUNT_COLLECTION)
        .document(accountId)
        .update("avatarUrl", avatarUrl)
        .addOnCompleteListener {
            continuation.resume(it.isSuccessful)
        }
}
