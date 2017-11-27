#include "ButtonInfo.h"

// 基本信息选择列表（材质-形状-线孔数-透明性-花色-主色）
QVector<QString> ButtonMaterialStrEnBuf = {"resin", "fruit", "shell", "metal", "jade"};
QVector<QString> ButtonMaterialStrCnBuf = {"树脂", "果实", "贝壳", "金属", "玉石"};
QVector<QString> ButtonShapeStrEnBuf = {"circle", "heart", "triangle", "square", "pentagon", "quincunx", "other"};
QVector<QString> ButtonShapeStrCnBuf = {"圆形", "心形", "三角形", "方形", "五角形", "梅花形", "异形"};
QVector<QString> ButtonHoleNumStrEnBuf = {"2", "4"};
QVector<QString> ButtonHoleNumStrCnBuf = {"2", "4"};
QVector<QString> ButtonLightStrEnBuf = {"transparent", "semiTransparent", "none"};
QVector<QString> ButtonLightStrCnBuf = {"透明", "半透明", "不透明"};
QVector<QString> ButtonPatternStrEnBuf = {"solidColor", "multiColor", "texture", "character", "picture"};
QVector<QString> ButtonPatternStrCnBuf = {"纯色", "拼色", "花纹", "字符", "图案"};
QVector<QString> ButtonColorStrEnBuf = {"white", "red", "orange", "yellow", "green", "cyan", "blue", "purple", "black"};
QVector<QString> ButtonColorStrCnBuf = {"白色", "红色", "橙色", "黄色", "绿色", "青色", "蓝色", "紫色", "黑色"};
QVector<QString> ButtonSizeBuf = {"0~10(mm)", "10~20(mm)", "20~30(mm)", "30~40(mm)"};
QMap<QString, qint32> ButtonMaterialMap = {{"resin",0}, {"fruit",1}, {"shell",2}, {"metal",3}, {"jade",4}};
QMap<QString, qint32> ButtonShapeMap = {{"circle",0}, {"heart",1}, {"triangle",2}, {"square",3}, {"pentagon",4}, {"quincunx",5}, {"other",6}};
QMap<QString, qint32> ButtonHoleNumMap = {{"2",0}, {"4",1}};
QMap<QString, qint32> ButtonLightMap = {{"transparent",0}, {"semiTransparent",1}, {"none",2}};
QMap<QString, qint32> ButtonPatternMap = {{"solidColor",0}, {"multiColor",1}, {"texture",2}, {"character",3}, {"picture",4}};
QMap<QString, qint32> ButtonColorMap = {{"white",0}, {"red",1}, {"orange",2}, {"yellow",3}, {"green",4}, {"cyan",5}, {"blue",6}, {"purple",7}, {"black",8}};
