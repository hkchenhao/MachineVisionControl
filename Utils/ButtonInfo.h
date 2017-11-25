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
#include <QString>
#include <QMap>

// 基本信息选择列表（材质-形状-线孔数-透明性-花色-主色）
extern QString ButtonMaterialStrEnBuf[];
extern QString ButtonMaterialStrCnBuf[];
extern QString ButtonShapeStrEnBuf[];
extern QString ButtonShapeStrCnBuf[];
extern QString ButtonHoleNumStrEnBuf[];
extern QString ButtonHoleNumStrCnBuf[];
extern QString ButtonLightStrEnBuf[];
extern QString ButtonLightStrCnBuf[];
extern QString ButtonPatternStrEnBuf[];
extern QString ButtonPatternStrCnBuf[];
extern QString ButtonColorStrEnBuf[];
extern QString ButtonColorStrCnBuf[];
extern QMap<QString, qint32> ButtonMaterialMap;
extern QMap<QString, qint32> ButtonShapeMap;
extern QMap<QString, qint32> ButtonHoleNumMap;
extern QMap<QString, qint32> ButtonLightMap;
extern QMap<QString, qint32> ButtonPatternMap;
extern QMap<QString, qint32> ButtonColorMap;

#endif
