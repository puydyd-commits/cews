import * as XLSX from 'xlsx'

export function exportToExcel(data, filename, columns) {
  const worksheetData = data.map(item => {
    const row = {}
    columns.forEach(col => {
      let value = item[col.prop]
      if (value === null || value === undefined) {
        value = ''
      }
      row[col.label] = value
    })
    return row
  })

  const worksheet = XLSX.utils.json_to_sheet(worksheetData)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '商品数据')

  const colWidths = columns.map(col => ({ wch: col.width || 15 }))
  worksheet['!cols'] = colWidths

  XLSX.writeFile(workbook, `${filename}.xlsx`)
}

export function exportProductToExcel(products, filename = '商品列表') {
  const columns = [
    { label: '商品ID', prop: 'id', width: 10 },
    { label: '商品名称', prop: 'productName', width: 25 },
    { label: '分类', prop: 'categoryName', width: 15 },
    { label: '价格', prop: 'price', width: 12 },
    { label: '库存', prop: 'stock', width: 10 },
    { label: '产地', prop: 'origin', width: 15 },
    { label: '创建人', prop: 'creator', width: 12 },
    { label: '创建时间', prop: 'createTime', width: 22 }
  ]

  const formattedProducts = products.map(product => ({
    ...product,
    price: `¥${product.price}`,
    createTime: formatDateTime(product.createTime)
  }))

  exportToExcel(formattedProducts, filename, columns)
}

function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}