package com.example.wifirtt.ui.data

import android.net.wifi.ScanResult

object ScanRTTRouters {
    private var scanForRTT: MutableList<ScanResult> = ArrayList()
    private var toScan: MutableList<RouterToScan> = ArrayList()

    fun addNewListForRTT(list: ArrayList<ScanResult>) {
        scanForRTT.clear()
        scanForRTT = list
    }
    fun getListForRTT() : MutableList<ScanResult> {
        return scanForRTT
    }
    fun clearForRTT() {
        scanForRTT.clear()
    }
    fun addForRTT(scan: ScanResult) {
        scanForRTT.add(scan)
    }
    fun orderRoutersToRanging(list: ArrayList<Router>) {
        toScan.clear()
        for (j in scanForRTT) {
            for (i in list) {
                if (i.bssid == j.BSSID) {
                    addToScan(RouterToScan(j, i.x, i.y, 0F, 0F))
                    break
                }
            }
        }
    }
    fun checkRouters(list: ArrayList<Router>): CheckRouter {
        toScan.clear()
        if (list.size < 3) return CheckRouter(0, null) //0 = nie wystarczająca liczba routerów na liście
        val notFound : ArrayList<String> = ArrayList()
        var check = true
        for (i in list) {
            check = true
            for (j in scanForRTT) {
                if (i.bssid == j.BSSID)  {
                    addToScan(RouterToScan(j, i.x, i.y, 0F, 0F))
                    check = false
                    break
                }
            }
            if (check) notFound.add(i.bssid)
        }
        orderRoutersToRanging(list)
        if (notFound.size == 0) {
            return CheckRouter(1, null) //1 = znaleziono wszystkie routery z listy
        }
        return if (toScan.size < 3) CheckRouter(2, notFound) //2 = nie znaleziono wszystkich routerów i przez to jest za mało routerów
        else CheckRouter(3, notFound) //3 = nie znaleziono wszystkich routerów, ale jest wystarczająca liczba routerów
    }
    fun addToScan(scan: RouterToScan) {
        toScan.add(scan)
    }
    fun getListToScan() : MutableList<RouterToScan> {
        return toScan
    }
    fun normalizeRouters(bb: BoundingBox4Params) {
        toScan.forEach {
            var help = it.x - bb.left
            it.xNorm = help
            help = bb.up - it.y
            it.yNorm = help
        }
    }
    fun getListToScanResult() : MutableList<ScanResult> {
        val list: MutableList<ScanResult> = ArrayList()
        for (i in getListToScan()) {
            list.add(i.scanResult)
        }
        return list
    }


}