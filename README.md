## Zebra プリンタ - Bluetooth 印刷デモ用のAndroid Apkとソースコード - Android 13 (ZQ220+用）

Android 13用
Update:2025/04/19
</br>

<img height="200" src="https://www.zebra.com/content/dam/zebra_dam/global/zcom-web-production/web-production-photography/web002/tc78-zq220-proof-of-delivery-printing-receipt-courier-photography-application-5x4-3600.jpg.imgw.3600.3600.jpg"> <img height="200" src="https://www.zebra.com/content/dam/zebra_dam/global/zcom-web-production/web-production-photography/web002/zq220-police-handing-ticket-photography-application-5x4-3600.jpg.imgw.3600.3600.jpg"> <img height="200" src="https://www.zebra.com/content/dam/zebra_dam/global/zcom-web-production/web-production-photography/web002/tc78-zq220-transportation-logistics-photography-application-5x4-3600.jpg.imgw.3600.3600.jpg">

### 概要
- Multiplatform SDKを用いて、ZQ220+から印刷するためのサンプルコード。
- Bluetooth 接続経由のラベル印刷パフォーマンスをデモするためのツール。
- Android 13用のBluetooth 接続経由のラベル印刷アプリを開発する方向けにソースコードを提供。

</br>

### テスト済み環境
- TC22, TC27/A13
- ZQ220+

</br>

### 開発環境
- Android Studio Coala
- Zebra Android 端末（Android 13)

</br>

### 機能

| function              | remarks                                                 |
| --------------------- | ------------------------------------------------------- |
| sendCpclOverBluetooth | Bluetooth経由でプリンタに印刷データ(CPCL)を送信         |
| sendSgdOverBluetooth  | Bluetooth経由でプリンタのステータス確認コマンドそ送受信 |

</br>


### 補足資料１：ステータス早見表

ZQ220+はLink-OSベーシックのため、プリンタ状態について基本的な情報のみ取得が可能。下記のSGDパラメータを組み合わせてプリンタの状態を判定する必要がある。

| Status                 | device.status | media.status | head.latch |
| ---------------------- | ------------- | ------------ | ---------- |
| 待機中、データ受信可能 | ready         | ok           | ok         |
| カバーオープン         | busy          | ?            | open       |
| 用紙切れ               | busy          | out          | ok         |
| Exception Error        | busy          | *            | *          |

</br>

### 補足資料２：プリンタ側の設定

Bluetooth接続前にプリンタ側で下記の設定をしておくこと。

```
! U1 setvar "bluetooth.discoverable" "on"
! U1 setvar "bluetooth.bluetooth.minimum_security_mode" "1"
! U1 setvar "bluetooth.enable" "on"
```


