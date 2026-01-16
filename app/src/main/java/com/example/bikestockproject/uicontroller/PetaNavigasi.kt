package com.example.bikestockproject.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bikestockproject.uicontroller.route.DestinasiHome
import com.example.bikestockproject.uicontroller.route.DestinasiLogin
import com.example.bikestockproject.uicontroller.route.DestinasiMerkEdit
import com.example.bikestockproject.uicontroller.route.DestinasiMerkEntry
import com.example.bikestockproject.uicontroller.route.DestinasiMerkList
import com.example.bikestockproject.uicontroller.route.DestinasiPenjualanDetail
import com.example.bikestockproject.uicontroller.route.DestinasiPenjualanEdit
import com.example.bikestockproject.uicontroller.route.DestinasiPenjualanEntry
import com.example.bikestockproject.uicontroller.route.DestinasiPenjualanList
import com.example.bikestockproject.uicontroller.route.DestinasiProdukDetail
import com.example.bikestockproject.uicontroller.route.DestinasiProdukEdit
import com.example.bikestockproject.uicontroller.route.DestinasiProdukEntry
import com.example.bikestockproject.uicontroller.route.DestinasiProdukList
import com.example.bikestockproject.uicontroller.route.DestinasiProdukStok
import com.example.bikestockproject.view.HomeScreen
import com.example.bikestockproject.view.LoginScreen
import com.example.bikestockproject.view.MerkEditScreen
import com.example.bikestockproject.view.MerkEntryScreen
import com.example.bikestockproject.view.MerkListScreen
import com.example.bikestockproject.view.PenjualanDetailScreen
import com.example.bikestockproject.view.PenjualanEditScreen
import com.example.bikestockproject.view.PenjualanEntryScreen
import com.example.bikestockproject.view.PenjualanListScreen
import com.example.bikestockproject.view.ProdukDetailScreen
import com.example.bikestockproject.view.ProdukEditScreen
import com.example.bikestockproject.view.ProdukEntryScreen
import com.example.bikestockproject.view.ProdukListScreen

import com.example.bikestockproject.view.SplashScreen

@Composable
fun BikeStockApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    HostNavigasi(navController = navController, modifier = modifier)
}

@Composable
fun HostNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        // ==================== SPLASH ====================
        composable("splash") {
            SplashScreen(
                onGetStarted = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        // ==================== AUTH ====================
        composable(DestinasiLogin.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(DestinasiHome.route) {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                }
            )
        }

        // ==================== HOME ====================
        composable(DestinasiHome.route) {
            HomeScreen(
                navigateToMerk = { navController.navigate(DestinasiMerkList.route) },
                navigateToPenjualan = { navController.navigate(DestinasiPenjualanList.route) },
                onLogout = {
                    navController.navigate(DestinasiLogin.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ==================== MERK ====================
        composable(DestinasiMerkList.route) {
            MerkListScreen(
                navigateToMerkEntry = { navController.navigate(DestinasiMerkEntry.route) },
                navigateToMerkEdit = { merkId ->
                    navController.navigate("${DestinasiMerkEdit.route}/$merkId")
                },
                navigateBack = { navController.navigateUp() },
                navigateToProdukByMerk = { merkId, merkName ->
                    navController.navigate("${DestinasiProdukList.route}?merkId=$merkId&merkName=$merkName")
                }
            )
        }

        composable(DestinasiMerkEntry.route) {
            MerkEntryScreen(navigateBack = { navController.navigateUp() })
        }

        composable(
            DestinasiMerkEdit.routeWithArgs,
            arguments = listOf(navArgument(DestinasiMerkEdit.merkIdArg) { type = NavType.IntType })
        ) {
            MerkEditScreen(navigateBack = { navController.navigateUp() })
        }

        // ==================== PRODUK ====================
        composable(
            DestinasiProdukList.routeWithArgs,
            arguments = listOf(
                navArgument(DestinasiProdukList.merkIdArg) { type = NavType.StringType; nullable = true },
                navArgument(DestinasiProdukList.merkNameArg) { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val merkIdString = backStackEntry.arguments?.getString(DestinasiProdukList.merkIdArg)
            val merkName = backStackEntry.arguments?.getString(DestinasiProdukList.merkNameArg)

            ProdukListScreen(
                merkId = merkIdString?.toIntOrNull(),
                merkName = merkName,
                navigateToProdukEntry = { id, nama ->
                    navController.navigate("${DestinasiProdukEntry.route}/$id/$nama")
                },
                navigateToProdukDetail = { produkId ->
                    navController.navigate("${DestinasiProdukDetail.route}/$produkId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = DestinasiProdukEntry.routeWithArgs,
            arguments = listOf(
                navArgument(DestinasiProdukEntry.merkIdArg) { type = NavType.IntType },
                navArgument(DestinasiProdukEntry.merkNameArg) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mNama = backStackEntry.arguments?.getString(DestinasiProdukEntry.merkNameArg) ?: ""
            ProdukEntryScreen(
                namaMerk = mNama,
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            DestinasiProdukDetail.routeWithArgs,
            arguments = listOf(navArgument(DestinasiProdukDetail.produkIdArg) { type = NavType.IntType })
        ) {
            ProdukDetailScreen(
                navigateToProdukEdit = { produkId ->
                    navController.navigate("${DestinasiProdukEdit.route}/$produkId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            DestinasiProdukEdit.routeWithArgs,
            arguments = listOf(navArgument(DestinasiProdukEdit.produkIdArg) { type = NavType.IntType })
        ) {
            ProdukEditScreen(navigateBack = { navController.navigateUp() })
        }

        // ==================== PENJUALAN ====================
        composable(DestinasiPenjualanList.route) {
            PenjualanListScreen(
                navigateToPenjualanEntry = { navController.navigate(DestinasiPenjualanEntry.route) },
                navigateToPenjualanDetail = { penjualanId ->
                    navController.navigate("${DestinasiPenjualanDetail.route}/$penjualanId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(DestinasiPenjualanEntry.route) {
            PenjualanEntryScreen(navigateBack = { navController.navigateUp() })
        }

        composable(
            DestinasiPenjualanDetail.routeWithArgs,
            arguments = listOf(navArgument(DestinasiPenjualanDetail.penjualanIdArg) { type = NavType.IntType })
        ) {
            // PARAMETER navigateToPenjualanEdit DIHAPUS agar sesuai dengan Screen Anda
            PenjualanDetailScreen(
                navigateToPenjualanEdit = { penjualanId ->
                    navController.navigate("${DestinasiPenjualanEdit.route}/$penjualanId")
                },
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            DestinasiPenjualanEdit.routeWithArgs,
            arguments = listOf(navArgument(DestinasiPenjualanEdit.penjualanIdArg) { type = NavType.IntType })
        ) {
            PenjualanEditScreen(navigateBack = { navController.navigateUp() })
        }
    }
}