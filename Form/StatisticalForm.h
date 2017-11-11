#ifndef STATISTICALFORM_H
#define STATISTICALFORM_H
#include <QWidget>
#include <QtCharts>
QT_CHARTS_USE_NAMESPACE

namespace Ui
{
    class StatisticalWidget;
}

class StatisticalForm : public QWidget
{
    Q_OBJECT
public:
    explicit StatisticalForm(QWidget* parent = 0);
    ~StatisticalForm();
private slots:
    void on_pushButton_Esc_clicked();

private:
    Ui::StatisticalWidget *ui;
};

#endif
