#!/bin/bash

# This script creates a complete Android project package
# that can be built with Android Studio or command line tools

echo "Creating APK package for Kfriends Chat App..."

# Create a compressed archive with all project files
cd /home/ubuntu
tar -czf KfriendsChatApp.tar.gz KfriendsChatApp/
ls -lh KfriendsChatApp.tar.gz

echo ""
echo "✅ Project package created successfully!"
echo ""
echo "📦 File: KfriendsChatApp.tar.gz"
echo ""
echo "Next steps:"
echo "1. Download the file to your computer"
echo "2. Extract it: tar -xzf KfriendsChatApp.tar.gz"
echo "3. Open in Android Studio: File → Open → Select KfriendsChatApp folder"
echo "4. Click Build → Build Bundle(s) / APK(s) → Build APK(s)"
echo ""
echo "Or use command line:"
echo "  cd KfriendsChatApp"
echo "  ./gradlew assembleDebug"
echo ""

