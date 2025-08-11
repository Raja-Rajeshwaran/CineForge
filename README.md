# 🎬 CineForge - AI Movie Plot Generator

**CineForge** is a Java Swing desktop app that fuses human creativity with the power of AI via **OpenRouter**. It dynamically generates compelling movie plots based on user input like genre, setting, tone, and keywords. Whether you're a screenwriter, student, or just vibing with story ideas — this tool helps you bring your film plots to life.

---

## 🔮 Features

- ✨ **LLM-Generated Plots** via OpenRouter
- 🎭 **Genre + Tone Based Prompts** for horror, drama, thriller, and more
- 💾 **Save Plot History** in MySQL for reference
- 🔁 **Regenerate** stories with one click
- 📤 **Export to TXT** for scriptwriting or pitching
- 🌚 **Cinematic Dark UI** with smooth transitions
- 👥 **Perfect for Creators** looking to collaborate with AI

---

## 🛠️ Tech Stack

- **Java Swing** - UI Framework
- **Java HTTP Client** - API Calls
- **MySQL** - Plot storage
- **OpenRouter API** - AI plot generation
- **No external frameworks** – pure Java project

---

## 📋 Prerequisites

- JDK 11+
- MySQL Server (8.0+ recommended)
- Eclipse / IntelliJ / VS Code (any Java IDE)
- OpenRouter API Key → [https://openrouter.ai](https://openrouter.ai)

---

## 🔑 Setup Your OpenRouter API

1. Go to [OpenRouter](https://openrouter.ai/)
2. Sign in with GitHub or email
3. Navigate to "API Keys" in your dashboard
4. Copy your API key
5. Paste it into `OpenRouterClient.java`:

