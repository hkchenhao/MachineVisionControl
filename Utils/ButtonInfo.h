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

extern QVector<QString> ButtonSizeStrBuf;
extern QVector<double> ButtonSizeBuf;

extern QMap<QString, qint32> ButtonMaterialEnMap;
extern QMap<QString, qint32> ButtonMaterialCnMap;
extern QMap<QString, qint32> ButtonShapeEnMap;
extern QMap<QString, qint32> ButtonShapeCnMap;
extern QMap<QString, qint32> ButtonHoleNumEnMap;
extern QMap<QString, qint32> ButtonHoleNumCnMap;
extern QMap<QString, qint32> ButtonLightEnMap;
extern QMap<QString, qint32> ButtonLightCnMap;
extern QMap<QString, qint32> ButtonPatternEnMap;
extern QMap<QString, qint32> ButtonPatternCnMap;
extern QMap<QString, qint32> ButtonColorEnMap;
extern QMap<QString, qint32> ButtonColorCnMap;

#endif
