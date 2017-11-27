#ifndef BUTTONINFO_H
#define BUTTONINFO_H
//#include <QWidget>
//#include <QCoreApplication>
//#include <QVBoxLayout>
//#include <QEvent>
//#include <QLabel>
//#include <QImage>
//#include <QPixmap>
//#include <QFileInfo>
#include <QVector>
#include <QString>
#include <QMap>

// 基本信息选择列表（材质-形状-线孔数-透明性-花色-主色）
extern QVector<QString> ButtonMaterialStrEnBuf;
extern QVector<QString> ButtonMaterialStrCnBuf;
extern QVector<QString> ButtonShapeStrEnBuf;
extern QVector<QString> ButtonShapeStrCnBuf;
extern QVector<QString> ButtonHoleNumStrEnBuf;
extern QVector<QString> ButtonHoleNumStrCnBuf;
extern QVector<QString> ButtonLightStrEnBuf;
extern QVector<QString> ButtonLightStrCnBuf;
extern QVector<QString> ButtonPatternStrEnBuf;
extern QVector<QString> ButtonPatternStrCnBuf;
extern QVector<QString> ButtonColorStrEnBuf;
extern QVector<QString> ButtonColorStrCnBuf;
extern QVector<QString> ButtonSizeBuf;
extern QMap<QString, qint32> ButtonMaterialMap;
extern QMap<QString, qint32> ButtonShapeMap;
extern QMap<QString, qint32> ButtonHoleNumMap;
extern QMap<QString, qint32> ButtonLightMap;
extern QMap<QString, qint32> ButtonPatternMap;
extern QMap<QString, qint32> ButtonColorMap;

#endif
