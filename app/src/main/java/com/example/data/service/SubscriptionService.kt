package com.example.data.service

enum class SubscriptionTier(val title: String, val priceNpr: String, val scanLimit: String) {
    FREE("Free Plan", "NPR 0", "5 Scans / month"),
    PRO("Krishi Pro", "NPR 499 / year", "Unlimited AI Scans"),
    EXPERT("Agro Expert Pass", "NPR 1,299 / year", "Pro + 1-on-1 Agronomist Calls")
}

data class UserEntitlement(
    val currentTier: SubscriptionTier = SubscriptionTier.FREE,
    val scansUsedThisMonth: Int = 2,
    val maxFreeScans: Int = 5,
    val isProActive: Boolean = false,
    val expiryDate: String? = null
)

interface SubscriptionService {
    suspend fun getEntitlement(userId: String): UserEntitlement
    suspend fun initiateUpgrade(userId: String, targetTier: SubscriptionTier): Result<String> // Returns payment intent checkout URL
}

class DefaultSubscriptionService : SubscriptionService {
    private var currentEntitlement = UserEntitlement()

    override suspend fun getEntitlement(userId: String): UserEntitlement {
        return currentEntitlement
    }

    override suspend fun initiateUpgrade(userId: String, targetTier: SubscriptionTier): Result<String> {
        currentEntitlement = currentEntitlement.copy(
            currentTier = targetTier,
            isProActive = targetTier != SubscriptionTier.FREE
        )
        return Result.success("https://payment.krishiai.np/checkout?tier=${targetTier.name}")
    }
}
