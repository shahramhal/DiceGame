# Dice Game - Android Application

(![Dice Game Screenshot](https://github.com/user-attachments/assets/b96b8bc7-f95b-4392-922b-a3976f8d3dbf)
) <!-- Add actual screenshot later -->

A Kotlin-based Android dice game application built with Jetpack Compose, implementing a competitive game between a human player and computer AI.

## Features

- 🎲 **Interactive Dice Gameplay**: Roll 5 dice against a computer opponent
- 🏆 **Smart Computer AI**: Adaptive strategy that considers game state (Task 12)
- ⚖️ **Tie-Breaking System**: Special rules for tied scores (Task 9)
- 🎯 **Custom Target Score**: Set your own winning score (Task 10)
- 📊 **Win Tracking**: Keeps count of human vs computer wins (Task 11)
- ♻️ **Orientation Support**: Maintains state during screen rotation
- 🎨 **Modern UI**: Clean, intuitive interface with Jetpack Compose

## Game Rules

1. Both players roll 5 dice simultaneously
2. Players can keep selected dice and reroll others (up to 2 rerolls)
3. First to reach the target score (default: 101) wins
4. If both reach the target in the same attempts, the highest score wins
5. If completely tied, players enter tie-break rounds

## Implementation Details

- **Technology Stack**:
  - Kotlin
  - Jetpack Compose
  - Android Studio

- **Key Components**:
  - `MainActivity.kt`: Home screen with New Game and About buttons
  - `Start.kt`: Main game logic and UI
  - Custom theme system (Light/Dark mode support)

- **Computer Strategy** (Task 12):
  The AI considers:
  - Current dice values
  - Score difference between players
  - Proximity to the target score
  - Implements both random and optimized strategies

## Screenshots
![Game Board ](https://github.com/user-attachments/assets/389bf036-6e96-4e42-ac74-8896bb0935c9)
![Winner Dialog ](https://github.com/user-attachments/assets/0c3c4850-36c5-40a5-a4d1-99fa16e257e0)
![Change the target score function](https://github.com/user-attachments/assets/87447f5d-cb63-4344-bfee-901d16dab3a0)




## Installation

1. Clone this repository:
   ```bash
   git clone https://github.com/shahramhal/DiceGame.git
