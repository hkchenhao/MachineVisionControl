-------------------------------------------------
#
# Project created by QtCreator 2016-12-25T19:11:02
#
#-------------------------------------------------

QT       += core gui \
            gui \
            network \
            charts \
            serialbus \
            serialport

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = MachineVisionControl
TEMPLATE = app

# The following define makes your compiler emit warnings if you use
# any feature of Qt which as been marked as deprecated (the exact warnings
# depend on your compiler). Please consult the documentation of the
# deprecated API in order to know how to port your code away from it.
DEFINES += QT_DEPRECATED_WARNINGS

# You can also make your code fail to compile if you use deprecated APIs.
# In order to do so, uncomment the following line.
# You can also select to disable deprecated APIs only up to a certain version of Qt.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0
INSTALLS += target
target.path = /home/pi
#target.path = /home/debian
#target.path = /root

SOURCES += \
    Form/MainForm.cpp \
    main.cpp \
    Event/MainFormEvent.cpp \
    Form/ButtonSelForm.cpp \
    Utils/ButtonInfo.cpp \
    Utils/SystemUtils.cpp \
    Form/StartForm.cpp \
    Mgr/FormFrame.cpp \
    Mgr/SystemFrame.cpp \
    Utils/FrmInput.cpp \
    Utils/QJsonAnalysis.cpp \
    Net/NetServer.cpp \
    Net/NetThread.cpp \
    Data/DataFactory.cpp \
    Data/DataPacket.cpp \
    Event/NotificationCenter.cpp \
    Form/StatisticalForm.cpp \
    Form/MachineLearnForm.cpp \
    Form/InfoCheckResForm.cpp \
    Utils/UserMsgBox.cpp \
    Form/InfoSizeForm.cpp \
    Form/SystemSetForm.cpp \
    Form/FileManagerForm.cpp \
    Form/HelpForm.cpp \
    Net/CanBusMgr.cpp

HEADERS  += \
    Form/MainForm.h \
    Event/MainFormEvent.h \
    Form/ButtonSelForm.h \
    Utils/ButtonInfo.h \
    Utils/SystemUtils.h \
    Form/StartForm.h \
    Mgr/FormFrame.h \
    Mgr/SystemFrame.h \
    Utils/FrmInput.h \
    Utils/QJsonAnalysis.h \
    Net/NetServer.h \
    Net/NetThread.h \
    Utils/SystemConstants.h \
    Data/DataFactory.h \
    Data/DataPacket.h \
    Data/DataPacketEnum.h \
    Event/NotificationCenter.h \
    Event/FormEventEnum.h \
    Form/StatisticalForm.h \
    Form/MachineLearnForm.h \
    Utils/WidgetStyle.h \
    Form/InfoCheckResForm.h \
    Utils/UserMsgBox.h \
    Form/InfoSizeForm.h \
    Form/SystemSetForm.h \
    Form/FileManagerForm.h \
    Form/HelpForm.h \
    Net/CanBusMgr.h

FORMS    += \
    Form/MainForm.ui \
    Form/ButtonSelForm.ui \
    Form/StartForm.ui \
    Form/FrmInput.ui \
    Form/StatisticalForm.ui \
    Form/MachineLearnForm.ui \
    Form/InfoCheckResForm.ui \
    Form/InfoSizeForm.ui \
    Form/SystemSetForm.ui \
    Form/FileManagerForm.ui \
    Form/HelpForm.ui

RESOURCES += \
    Res/Resource.qrc
