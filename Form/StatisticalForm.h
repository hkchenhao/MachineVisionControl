#ifndef STATISTICALFORM_H
#define STATISTICALFORM_H
#include <QWidget>
#include <QtCharts>
#include <QPainter>
#include <QProxyStyle>
#include <QStyleOptionTab>

QT_CHARTS_USE_NAMESPACE

// TabWidget样式表
class CustomTabStyle : public QProxyStyle
{
public:
    QSize sizeFromContents(ContentsType type, const QStyleOption* option, const QSize& size, const QWidget* widget) const
    {
        QSize s = QProxyStyle::sizeFromContents(type, option, size, widget);
        if (type == QStyle::CT_TabBarTab)
        {
            s.transpose();
            s.rwidth() = 170; // 设置每个tabBar中item的大小
            s.rheight() = 60;
        }
        return s;
    }
    void drawControl(ControlElement element, const QStyleOption* option, QPainter* painter, const QWidget* widget) const
    {
        if (element == CE_TabBarTabLabel)
        {
            if (const QStyleOptionTab* tab = qstyleoption_cast<const QStyleOptionTab*>(option))
            {
                QRect allRect = tab->rect;
                if (tab->state & QStyle::State_Selected)
                {
                    painter->save();
                    painter->setPen(0x89CFFF);
                    painter->setBrush(QBrush(0x89CFFF));
                    painter->drawRect(allRect.adjusted(6, 6, -6, -6));
                    painter->restore();
                }
                QTextOption option;
                option.setAlignment(Qt::AlignCenter);
                if (tab->state & QStyle::State_Selected)
                    painter->setPen(0xF8FCFF);
                else
                    painter->setPen(0x5D5D5D);
                painter->drawText(allRect, tab->text, option);
                return;
            }
        }
        if (element == CE_TabBarTab)
            QProxyStyle::drawControl(element, option, painter, widget);
    }
};

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
