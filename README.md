<div align="center">
  <img src="assets/logo.png" alt="NetClassroom Logo" width="240"/>
</div>

# 🏫 NetClassroom — TCP Networking Classroom System

A TCP-based digital classroom platform supporting real-time communication, private messaging, assignment management, and file submission using pure socket programming.

---

## 📑 Table of Contents
<details open>
  <summary><b>Expand</b></summary>

  - [📸 Screenshots](#-screenshots)
  - [📚 Project Overview](#-project-overview)
  - [✨ Features](#-features)
  - [🧩 Networking Concepts Applied](#-networking-concepts-applied)
  - [🏗️ Architecture](#-architecture)
  - [🛠️ Tech Stack](#-tech-stack)
  - [📋 Prerequisites](#-prerequisites)
  - [🚀 Installation & Setup](#-installation--setup)
  - [🧑‍💻 Usage](#-usage)
  - [📂 File Structure](#-file-structure)
  - [🧪 Result Highlights](#-result-highlights)
  - [⚠️ Limitations & Future Work](#️-limitations--future-work)
  - [👥 Contributors](#-contributors)
  - [📄 Resources](#-resources)
  - [📩 Support & Contact](#-support--contact)
</details>

---

## 📸 Screenshots

<div align="center">
  <img src="assets/screens/login.png" width="350"/>
  <img src="assets/screens/chat.png" width="350"/>
  <img src="assets/screens/groups.png" width="350"/>
  <img src="assets/screens/assignment.png" width="350"/>
</div>

---

## 📚 Project Overview

**NetClassroom** is a custom networked classroom system built entirely using:

✔ TCP socket programming  
✔ Multi-threaded client–server communication  
✔ JavaFX graphical interface for enhanced UX  

The platform demonstrates core computer networking concepts including:

> socket programming, session management, reliable data transfer, flow control, congestion control, and multiplexing

Developed as part of the **CSE-3111 Computer Networking Project** at the **University of Dhaka**.

---

## ✨ Features

### 👤 User Roles
- **Teacher**
- **Student**

### 💬 Communication
- Public classroom chat
- Private direct messaging (Teacher ↔ Student, Student ↔ Student)

### 📂 Assignments
- Create **solo** & **group** assignments
- Dynamic group formation
- File submission
- Submission tracking & status logs

### 🖥️ Clients
- CLI Terminal client
- JavaFX GUI client

---

## 🧩 Networking Concepts Applied

| Concept | Implementation |
|---|---|
| **Socket Programming** | Persistent TCP sockets for communication |
| **Client–Server Model** | Server routes & broadcasts client requests |
| **Reliable Data Transfer** | TCP ensures ordered and lossless messages |
| **Flow Control** | TCP auto-adjusts for receiver buffer |
| **Congestion Control** | TCP cwnd adapts under file uploads |
| **Session Management** | Per-client user session objects |
| **Multiplexing** | Multi-threaded connection handling |

---
## 🏗️ Project Architecture

├── src/
│ ├── client/
│ │ ├── ClassroomApp.java
│ │ ├── LoginController.java
│ │ ├── ChatController.java
│ │ ├── AssignmentController.java
│ │ ├── GroupController.java
│ │ └── ... (GUI & CLI Client Modules)
│ ├── server/
│ │ ├── TCPServer.java
│ │ ├── ClientHandler.java
│ │ ├── SessionManager.java
│ │ ├── ChatRoomManager.java
│ │ ├── AssignmentManager.java
│ │ ├── GroupManager.java
│ │ └── FileStorageHandler.java
│ ├── protocol/
│ │ ├── RequestParser.java
│ │ ├── ResponseBuilder.java
│ │ ├── PacketTypes.java
│ │ └── CustomProtocol.md
│ ├── utils/
│ │ ├── FileUtils.java
│ │ └── IOUtils.java
│
├── assets/
│ ├── logo.png
│ ├── screens/
│ │ ├── login.png
│ │ ├── chat.png
│ │ ├── submissions.png
│ │ └── groups.png
│ └── report/
│ ├── NetClassroom_Report.pdf
│ └── Presentation.pdf
│
├── out/ # compiled .class files
├── README.md
├── LICENSE
└── run.sh / run.bat # optional startup scripts


---

## 🧑‍💻 Usage

### 👨‍🏫 **Teacher Features**
- Create solo & group assignments
- Push announcements to students
- View submission status
- Download submitted files
- Private chat with students
- Public classroom messaging

### 🎓 **Student Features**
- Join as authenticated student
- Receive announcements
- Submit assignment files
- Join group assignments
- Public chat participation
- Private chat with teacher

---

## 🛡️ Security & Networking Features

✔ **Persistent TCP Socket Connections** (Server ↔ Client)  
✔ **Reliable Data Transfer via TCP** (file + chat messages)  
✔ **Multi-threaded Server Handling** (one thread per client)  
✔ **Role-based Session Management**  
✔ **Message Routing & Multiplexing**  
✔ **Custom HTTP-like Application Protocol**  
✔ **Content-Length framing for payload separation**

**Note:** Encryption (TLS/SSL) is not implemented yet.

---

## 🤝 Contributing

We welcome improvements 🤝

1. Fork the repository  
2. Create a feature branch:
   ```bash
   git checkout -b feature/new-feature
Commit changes:

git commit -m "Add new feature"


Push branch:

git push origin feature/new-feature


Open a Pull Request 🎉

👥 Contributors
<div align="center">
🚀 Project Team
<table> <tr> <td align="center"> <img src="assets/about/aditto.jpg" width="100px;" height="100px;" alt="Aditto Raihan" style="border-radius: 50%; object-fit: cover;"/> <br> <sub><b>Aditto Raihan</b></sub> <br> <a href="https://github.com/aditto">💻 ⚙️</a> <br> <small>Networking & Backend</small> </td> <td align="center"> <img src="assets/about/jubair.jpg" width="100px;" height="100px;" alt="Jubair Ahammad Akter" style="border-radius: 50%; object-fit: cover;"/> <br> <sub><b>Jubair Ahammad Akter</b></sub> <br> <a href="https://github.com/Jubair-Adib">💻 🎨 🖥️</a> <br> <small>Client UI + Protocol Design</small> </td> </tr> </table> </div>
📄 License

This project is for academic and educational purposes.
You may adapt & extend with proper attribution.

<div align="center"> <img src="assets/logo.png" alt="NetClassroom Logo" width="200"/> </div>
📦 Support & Documentation

📧 Email: akteradib007@gmail.com

📊 Presentation: NetClassroom_Presentation.pdf

📑 Report: NetClassroom_Report.pdf

🎥 Demo (optional): Coming Soon…

<div align="center">

Built with ❤️ using TCP, Java, Threads & JavaFX

Making classrooms more connected, one packet at a time.

</div>
