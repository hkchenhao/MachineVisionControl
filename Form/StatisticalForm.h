#ifndef STATISTICALFORM_H
#define STATISTICALFORM_H
#include <QWidget>
#include <QtCharts>
#include "Utils/QJsonAnalysis.h"
QT_CHARTS_USE_NAMESPACE

namespace Ui
{
    class StatisticalWidget;
}

// 纽扣检测结果结构体
struct ButtonStatisticalInfo
{
    // 检测结果
    qint32 right_num;
    qint32 wrong_num;
    // 缺陷类型
    qint32 wrongtypebuf[16];
//    qint32 DEF_EDGE;
//    qint32 DEF_HOLE;
//    qint32 DEF_SIZE;
//    qint32 DEF_FORE;
//    qint32 DEF_PITS;
//    qint32 DEF_BLOB;
//    qint32 DEF_UNFI;
//    qint32 DEF_HOLE_MORE;
//    qint32 DEF_HOLE_LESS;
//    qint32 DEF_HOLE_SHIFT;
//    qint32 DEF_OTHR;
    // json文件
    QJsonAnalysis jsoninfo;
};

class StatisticalForm : public QWidget
{
    Q_OBJECT
public:
    explicit StatisticalForm(QWidget* parent = 0);
    ~StatisticalForm();
private slots:
    void on_pushButton_Esc_clicked();
    void on_pushButton_SavePic_clicked();

private:
    Ui::StatisticalWidget *ui;
};

#endif
