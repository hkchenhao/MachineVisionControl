#include "QJsonAnalysis.h"
#include <QDebug>
#include <QFile>
#include <QTextStream>
#include <QRegularExpression>

/***********************************************************************************************************************
 *                                                    JsonPrivate                                                      *
 **********************************************************************************************************************/
JsonPrivate::JsonPrivate(const QString &jsonOrJsonFilePath, bool fromFile)
{
    // Json的内容
    QByteArray json("{}");
    // 如果传入的是Json文件的路径，则读取内容
    if(fromFile)
    {
        QFile file(jsonOrJsonFilePath);
        if (file.open(QIODevice::ReadOnly | QIODevice::Text))
            json = file.readAll();
        else
            qDebug() << QString("Cannot open the file: %1").arg(jsonOrJsonFilePath);
    }
    else
    {
        json = jsonOrJsonFilePath.toUtf8();
    }
    // 解析Json
    QJsonParseError error;
    QJsonDocument jsonDocument = QJsonDocument::fromJson(json, &error);
    root = jsonDocument.object();
    if(QJsonParseError::NoError != error.error)
    {
        qDebug() << error.errorString() << ", Offset: " << error.offset;
    }
}

JsonPrivate::JsonPrivate(const QByteArray &jsonByteArray)
{
    QJsonParseError error;
    QJsonDocument jsonDocument = QJsonDocument::fromJson(jsonByteArray, &error);
    root = jsonDocument.object();
    if(QJsonParseError::NoError != error.error)
    {
        qDebug() << error.errorString() << ", Offset: " << error.offset;
    }
}

// 使用递归与引用设置Json的值，因为toObject()等返回的是对象的副本，对其修改不会改变原来的对象，所以需要用引用来实现
void JsonPrivate::setValue(QJsonObject &parent, const QString &path, const QJsonValue &newValue)
{
    const int indexOfDot = path.indexOf('.');
    const QString property = path.left(indexOfDot);                                 // 第一个.之前的内容，如果indexOfDot是-1则返回整个字符串
    const QString subPath = (indexOfDot>0) ? path.mid(indexOfDot+1) : QString();    // 第一个.后面的内容
    QJsonValue subValue = parent[property];
    if(subPath.isEmpty())
    {
        subValue = newValue;
    }
    else
    {
        QJsonObject obj = subValue.toObject();
        setValue(obj, subPath, newValue);
        subValue = obj;
    }
    parent[property] = subValue;
}

// 读取属性的值，如果fromNode为空，则从跟节点开始访问
QJsonValue JsonPrivate::getValue(const QString &path, const QJsonObject &fromNode) const
{
    QJsonObject parent(fromNode.isEmpty() ? root : fromNode);
    QStringList tokens = path.split(QRegularExpression("\\."));
    int size = tokens.size();
    // 定位到要访问的属性的parent，如"user.address.street"，要访问的属性"street"的parent是"address"
    for(int i = 0; i < size - 1; ++i)
    {
        if(parent.isEmpty())
            return QJsonValue();
        parent = parent.value(tokens.at(i)).toObject();
    }
    return parent.value(tokens.last());
}

/***********************************************************************************************************************
 *                                                  QJsonAnalysis                                                      *
 **********************************************************************************************************************/
QJsonAnalysis::QJsonAnalysis(const QString &jsonOrJsonFilePath, bool fromFile) : jsondata(new JsonPrivate(jsonOrJsonFilePath, fromFile))
{

}

QJsonAnalysis::QJsonAnalysis(const QByteArray &jsonByteArray) : jsondata(new JsonPrivate(jsonByteArray))
{

}

QJsonAnalysis::~QJsonAnalysis()
{
    delete jsondata;
}

int QJsonAnalysis::getInt(const QString &path, int def, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toInt(def);
}

bool QJsonAnalysis::getBool(const QString &path, bool def, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toBool(def);
}

double QJsonAnalysis::getDouble(const QString &path, double def, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toDouble(def);
}

QString QJsonAnalysis::getString(const QString &path, const QString &def, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toString(def);
}

QStringList QJsonAnalysis::getStringList(const QString &path, const QJsonObject &fromNode) const
{
    QStringList result;
    QJsonArray array = getJsonValue(path, fromNode).toArray();
    for(QJsonArray::const_iterator iter = array.begin(); iter != array.end(); ++iter)
    {
        QJsonValue value = *iter;
        result << value.toString();
    }
    return result;
}

QJsonArray QJsonAnalysis::getJsonArray(const QString &path, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toArray();
}

QJsonObject QJsonAnalysis::getJsonObject(const QString &path, const QJsonObject &fromNode) const
{
    return getJsonValue(path, fromNode).toObject();
}

QJsonValue QJsonAnalysis::getJsonValue(const QString &path, const QJsonObject &fromNode) const
{
    return jsondata->getValue(path, fromNode);
}

QByteArray QJsonAnalysis::getJsonRawByte()
{
    return QJsonDocument(jsondata->root).toJson();
}

void QJsonAnalysis::set(const QString &path, const QJsonValue &value)
{
    jsondata->setValue(jsondata->root, path, value);
}

void QJsonAnalysis::set(const QString &path, const QStringList &strings)
{
    QJsonArray array;
    foreach(const QString &str, strings)
    {
        array.append(str);
    }
    jsondata->setValue(jsondata->root, path, array);
}

void QJsonAnalysis::save(const QString &path, QJsonDocument::JsonFormat format)
{
    QFile file(path);
    if (!file.open(QIODevice::WriteOnly | QIODevice::Truncate | QIODevice::Text))
    {
        return;
    }
    QTextStream out(&file);
    out << QJsonDocument(jsondata->root).toJson(format);
}

QString QJsonAnalysis::toString(QJsonDocument::JsonFormat format) const
{
    return QJsonDocument(jsondata->root).toJson(format);
}

