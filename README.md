# Tài khoản trải nghiệm:
- Khách: johndoe@gmail.com, Johnuser1 .
- Nhân viên: janesmith@gmail.com, Userstaff1 .
- Quản trị: adminsmith@gmail.com, Useradmin1 .
- Link cho API real-time firebase: "https://de3ddfootball-default-rtdb.firebaseio.com/" 😭😭🤡 .

# Project Name
- Đặt sân đá bóng ĐỐNG ĐẾ - ĐỐNG ĐA
## Requirements
- Android Studio 2024.x
- SDK Version 29 or higher
- Firebase account and google-services.json (contact project owner (in project)) 

## Build Instructions
1. Clone the repository.
2. Open the project in Android Studio.
3. Make sure you have the required dependencies installed.
4. Build the app by selecting 'Build > Build APK'.
5. Run the app on your emulator or physical device.

# dữ liệu lưu trên real-time firebase mẫu
{
  "users": {
    "userId_1": {
      "name": "John Doe",
      "email": "johndoe@gmail.com",
      "role": "user", 
      "phone": "0123456789",
      "password": "Johnuser1", 
      "bookings": {
        "bookingId_1": true,  
        "bookingId_2": true
      }
    },
    "userId_2": {
      "name": "Jane Smith",
      "email": "janesmith@gmail.com",
      "role": "staff",
      "phone": "0123456788", 
      "password": "Userstaff1",
      "shiftId": "shiftId_1"
    },
    "userId_3": {
      "name": "Admin Smith",
      "email": "adminsmith@gmail.com",
      "role": "admin",
      "phone": "0123456778", 
      "password": "Useradmin1"
    }
}
,
  
  "stadiums": {
    "stadiumId_1": {
      "name": "Sân ABC",
      "location": "123 Đường A, Quận B",
      "price": 200000,
      "available_times": {  
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {
        "bookingId_1": true,  
        "bookingId_2": true
      }
    },
    "stadiumId_2": {
      "name": "Sân XYZ",
      "location": "456 Đường B, Quận C",
      "price": 250000,
      "available_times": {
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    },
    "stadiumId_3": {
      "name": "Sân ABC",
      "location": "123 Đường A, Quận B",
      "price": 200000,
      "available_times": {  
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    },
    "stadiumId_4": {
      "name": "Sân XYZ",
      "location": "456 Đường B, Quận C",
      "price": 250000,
      "available_times": {
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    },
    "stadiumId_5": {
      "name": "Sân XYZ",
      "location": "456 Đường B, Quận C",
      "price": 250000,
      "available_times": {
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    },
    "stadiumId_6": {
      "name": "Sân XYZ",
      "location": "456 Đường B, Quận C",
      "price": 250000,
      "available_times": {
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    },
    "stadiumId_7": {
      "name": "Sân XYZ",
      "location": "456 Đường B, Quận C",
      "price": 250000,
      "available_times": {
        "06:00-08:00": true,
        "08:00-10:00": true,
        "10:00-12:00": true,
        "12:00-14:00": true,
        "14:00-16:00": true,
        "16:00-18:00": true,
        "18:00-20:00": true,
        "20:00-22:00": true
      },
      "bookings": {}
    }
  },

  "bookings": {
    "bookingId_1": {
      "userId": "userId_1",
      "stadiumId": "stadiumId_1",
      "time": "08:00-10:00",
      "date": "2024-09-25",
      "status": "pending",  
      "paymentId": "paymentId_1",
      "services": {
        "serviceId_1": true,
        "serviceId_2": true
      }
    },
    "bookingId_2": {
      "userId": "userId_2",
      "stadiumId": "stadiumId_2",
      "time": "10:00-12:00",
      "date": "2024-09-26",
      "status": "confirmed",
      "paymentId": "paymentId_2",
      "services": {
        "serviceId_1": true
      }
    }
  }
  ,

  "payments": {
    "paymentId_1": {
      "bookingId": "bookingId_1",
      "amount": 220000,  
      "method": "cash",  
      "status": "paid" 
    },
    "paymentId_2": {
      "bookingId": "bookingId_2",
      "amount": 270000,  
      "method": "online",
      "status": "paid"
    }
  },

  "shifts": {
    "shiftId_1": {
      "staffId": "userId_2",
      "startTime": "2024-09-25 08:00",
      "endTime": "2024-09-25 16:00",
      "handover": "userId_3",  
      "transactions": {
        "transactionId_1": true,
        "transactionId_2": true
      }
    }
  },

  "services": {
    "serviceId_1": {
      "name": "Nước uống",
      "price": 10000,
      "description": "Nước suối đóng chai",
      "available": true
    },
    "serviceId_2": {
      "name": "Khăn lạnh",
      "price": 5000,
      "description": "Khăn ướt dùng một lần",
      "available": true
    }
  },

  "transactions": {
    "transactionId_1": {
      "shiftId": "shiftId_1",
      "serviceId": "serviceId_1",
      "amount": 10000,
      "time": "2024-09-25 10:00",
      "status": "completed"
    },
    "transactionId_2": {
      "shiftId": "shiftId_1",
      "serviceId": "serviceId_2",
      "amount": 5000,
      "time": "2024-09-25 11:00",
      "status": "completed"
    }
  },
"invoices":{
   "invoiceId_1":{	
	"userId": "userId_2",
	"stadiumId": "stadiumId_2",
	"bookingId": "bookingId_2",
	"name": "Nguyễn Văn A",
	"phone": "0345749211",
	"stadiumName": "2 3 4",
	"bookingTime":"06:00-08:00 08:00-10:00",
	"time":"2024-11-09 19:14",
	"stadiumPrice": 200000,
	"servicePrice": 25000,
	"surcharge": 0,
	"note":"abc",
	"status": cash,
	"mGuesst": 1500000,
	"sBack":1000000,
	"total": 225000
   },
 }
}
